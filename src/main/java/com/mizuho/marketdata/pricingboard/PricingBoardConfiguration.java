package com.mizuho.marketdata.pricingboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mizuho.marketdata.pricingboard.external.InboundPricing;
import com.mizuho.marketdata.pricingboard.external.OutboundPricing;
import com.mizuho.marketdata.pricingboard.external.PricingResource;
import com.mizuho.marketdata.pricingboard.external.exception.CatchAllExceptionMapper;
import com.mizuho.marketdata.pricingboard.external.exception.InvalidPricingExceptionMapper;
import com.mizuho.marketdata.pricingboard.infrastructure.InMemoryPricingRepository;
import com.mizuho.marketdata.pricingboard.pricing.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.jms.ConnectionFactory;
import javax.ws.rs.ApplicationPath;

import static javax.jms.Session.SESSION_TRANSACTED;
import static org.springframework.integration.dsl.IntegrationFlows.from;
import static org.springframework.integration.dsl.Transformers.fromJson;
import static org.springframework.integration.dsl.Transformers.toJson;

@Configuration
@EnableScheduling
@ApplicationPath("/marketplace")
@SuppressWarnings({"unused", "WeakerAccess"})
public class PricingBoardConfiguration extends ResourceConfig {
    public static final String VENDOR_X_INBOUND_QUEUE = "VendorX-Inbound";
    public static final String VENDOR_Y_INBOUND_QUEUE = "VendorY-Inbound";
    public static final String VENDOR_X = "VendorX";
    public static final String VENDOR_Y = "VendorY";

    public static final String DLQ = "DeadLetters";
    public static final String OUTBOUND_TOPIC = "Outbound";
    public static final String VENDOR_HEADER = "vendor";
    public static final String INSTRUMENT_HEADER = "instrument";

    public PricingBoardConfiguration() {
        register(PricingResource.class);
        register(InvalidPricingExceptionMapper.class);
        register(CatchAllExceptionMapper.class);
    }

    @Bean
    public PricingIdGenerator pricingIdGenerator() {
        return new UUIDPricingIdGenerator();
    }

    @Bean
    public PricingEnricher pricingEnricher() {
        return new PricingEnricher(pricingIdGenerator());
    }

    @Bean
    public InMemoryPricingRepository pricingRepository() {
        return new InMemoryPricingRepository();
    }

    @Bean
    public PricingManagementService pricingManagementService() {
        return new PricingManagementService(pricingRepository());
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void evictEligiblePricingsFromInMemoryRepository() {
        pricingRepository().evictEligible();
    }

    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");
    }

    @Bean
    public IntegrationFlow vendorXFlow() {
        return from(Jms.messageDrivenChannelAdapter(jmsConnectionFactory())
                    .destination(VENDOR_X_INBOUND_QUEUE)
                    .errorChannel(errors())
                    .configureListenerContainer(c -> c.sessionAcknowledgeMode(SESSION_TRANSACTED)))
                .channel(VENDOR_X)
                .transform(fromJson(InboundPricing.class))
                .transform(InboundPricing.class, p -> p.copy().forVendor(VENDOR_X).build())
                .channel(vendorAgnosticInput())
                .get();
    }

    @Bean
    public IntegrationFlow vendorYFlow() {
        return from(Jms.messageDrivenChannelAdapter(jmsConnectionFactory())
                    .destination(VENDOR_Y_INBOUND_QUEUE)
                    .errorChannel(errors())
                    .configureListenerContainer(c -> c.sessionAcknowledgeMode(SESSION_TRANSACTED)))
                .channel(VENDOR_Y)
                .transform(fromJson(InboundPricing.class))
                .transform(InboundPricing.class, p -> p.copy().forVendor(VENDOR_Y).build())
                .channel(vendorAgnosticInput())
                .get();
    }

    @Bean
    public IntegrationFlow mainFlow() {
        return from(vendorAgnosticInput())
                .transform(InboundPricing.class, InboundPricing::toPricing)
                .transform(Pricing.class, p -> pricingEnricher().enrich(p))
                .wireTap(p -> p.handle(pricingRegistration()))
                .transform(Pricing.class, OutboundPricing::fromPricing)
                .enrichHeaders(e -> e.<OutboundPricing>headerFunction(VENDOR_HEADER, m -> m.getPayload().vendorId()))
                .enrichHeaders(e -> e.<OutboundPricing>headerFunction(INSTRUMENT_HEADER, m -> m.getPayload().instrumentId()))
                .transform(toJson())
                .handle(Jms.outboundAdapter(jmsConnectionFactory())
                        .destination(OUTBOUND_TOPIC)
                        .configureJmsTemplate(c -> c.sessionTransacted(true)))
                .get();
    }

    @Bean
    public IntegrationFlow errorFlow() {
        return from(errors())
                .log(LoggingHandler.Level.ERROR)
                .handle(Jms.outboundAdapter(jmsConnectionFactory())
                        .destination(DLQ)
                        .configureJmsTemplate(c -> c.sessionTransacted(true)))
                .get();
    }

    @Bean
    public MessageChannel vendorAgnosticInput() {
        return MessageChannels.direct("vendor-agnostic-input").get();
    }

    @Bean
    public MessageChannel errors() {
        return MessageChannels.direct("errors").get();
    }

    @Bean
    public GenericHandler<Pricing> pricingRegistration() {
        return (p, headers) -> {
            pricingManagementService().registerPricing(p);
            return null;
        };
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public MessageConverter jacksonConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setObjectMapper(objectMapper());
        return converter;
    }

}
