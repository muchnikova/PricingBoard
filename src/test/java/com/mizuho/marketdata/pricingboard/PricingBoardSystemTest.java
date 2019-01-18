package com.mizuho.marketdata.pricingboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import com.mizuho.marketdata.pricingboard.external.InboundPricing;
import com.mizuho.marketdata.pricingboard.external.OutboundPricing;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.jms.Message;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.mizuho.marketdata.pricingboard.PricingBoardConfiguration.*;
import static com.mizuho.marketdata.pricingboard.external.InboundPricing.Builder.anInboundPricing;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static io.restassured.http.ContentType.JSON;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PricingBoardSystemTest {
    private static final String BASE_PATH = "/marketplace/board/pricing";
    private static final Type PRICING_LIST = new TypeToken<List<OutboundPricing>>(){}.getType();
    private static final BigDecimal TWENTY = new BigDecimal(20);
    private static final BigDecimal TEN = new BigDecimal(10);
    private static final BigDecimal ELEVEN = new BigDecimal(11);
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final LocalDateTime YESTERDAY = NOW.minusDays(1);
    private static volatile boolean initialized = false;

    @LocalServerPort
    private int serverPortNumber;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void registerTestPricings() {
        if (!initialized) {
            registerNewPricing(anInboundPricing()
                    .forInstrument("I1").forVendor("V1").forTicker("AAA.A").withPrice(TEN).withPriceDateTime(NOW).build());
            registerNewPricing(anInboundPricing()
                    .forInstrument("I1").forVendor("V2").forTicker("BBB.B").withPrice(TWENTY).withPriceDateTime(YESTERDAY).build());

            registerNewPricing(anInboundPricing()
                    .forInstrument("I2").forVendor("V1").forTicker("CCC.C").withPrice(ELEVEN).withPriceDateTime(YESTERDAY).build());
            registerNewPricing(anInboundPricing()
                    .forInstrument("I2").forVendor("V2").forTicker("DDD.D").withPrice(TEN).withPriceDateTime(NOW).build());

            initialized = true;
        }
    }

    @Test
    public void should_post_pricing() {
        InboundPricing inboundPricing = anInboundPricing()
                .forInstrument("I3").forVendor("V3").forTicker("EEE.E").withPrice(TWENTY).withPriceDateTime(NOW).build();

        given().port(serverPortNumber).and().basePath(BASE_PATH).and()
                .body(inboundPricing).with().contentType(JSON).log().body()
                .when().post()
                .then().assertThat().statusCode(is(OK.getStatusCode()));
    }

    @Test
    public void should_return_pricing_for_instrument_id() {
        String instrumentId = "I1";
        List<OutboundPricing> instrumentPricings = with().port(serverPortNumber).and().basePath(BASE_PATH)
                .get("/instrument/" + instrumentId)
                .then().log().body().and().extract().response().as(PRICING_LIST);
        assertThat(instrumentPricings.size(), is(2));

        assertThat(instrumentPricings.stream().map(OutboundPricing::vendorId).collect(toList()),
                containsInAnyOrder("V1", "V2"));

        assertThat(instrumentPricings.stream().map(OutboundPricing::ticker).collect(toList()),
                containsInAnyOrder("AAA.A", "BBB.B"));

        assertThat(instrumentPricings.stream().map(OutboundPricing::price).collect(toList()),
                containsInAnyOrder(TEN, TWENTY));

        assertThat(instrumentPricings.stream().map(OutboundPricing::priceDateTime).collect(toList()),
                containsInAnyOrder(NOW, YESTERDAY));
    }

    @Test
    public void should_return_pricing_for_vendor_id() {
        String vendorId = "V1";
        List<OutboundPricing> vendorPricings = with().port(serverPortNumber).and().basePath(BASE_PATH)
                .get("/vendor/" + vendorId)
                .then().log().body().and().extract().response().as(PRICING_LIST);

        assertThat(vendorPricings.size(), is(2));

        assertThat(vendorPricings.stream().map(OutboundPricing::instrumentId).collect(toList()),
                containsInAnyOrder("I1", "I2"));

        assertThat(vendorPricings.stream().map(OutboundPricing::ticker).collect(toList()),
                containsInAnyOrder("AAA.A", "CCC.C"));

        assertThat(vendorPricings.stream().map(OutboundPricing::price).collect(toList()),
                containsInAnyOrder(TEN, ELEVEN));

        assertThat(vendorPricings.stream().map(OutboundPricing::priceDateTime).collect(toList()),
                containsInAnyOrder(NOW, YESTERDAY));
    }

    @Test(timeout = 10000L)
    public void should_retrieve_pricing_registered_over_jms() throws Exception {
        String instrument  = randomUUID().toString();
        InboundPricing inboundPricingOne = anInboundPricing()
                .forInstrument(randomUUID().toString()).forTicker("AAA.A").withPrice(TEN).withPriceDateTime(NOW).build();
        InboundPricing inboundPricingTwo = anInboundPricing()
                .forInstrument(instrument).forTicker("AAA.A").withPrice(TWENTY).withPriceDateTime(NOW).build();
        InboundPricing inboundPricingThree = anInboundPricing()
                .forInstrument(instrument).forTicker("BBB.B").withPrice(TEN).withPriceDateTime(NOW).build();

        jmsTemplate.convertAndSend(VENDOR_X_INBOUND_QUEUE, inboundPricingOne);
        jmsTemplate.convertAndSend(VENDOR_Y_INBOUND_QUEUE, inboundPricingTwo);
        jmsTemplate.convertAndSend(VENDOR_X_INBOUND_QUEUE, inboundPricingThree);

        OutboundPricing outboundPricing = toOutboundPricing(
                jmsTemplate.receiveSelected(OUTBOUND_TOPIC, format("%s='%s' AND %s='%s'", VENDOR_HEADER, VENDOR_X, INSTRUMENT_HEADER, instrument)));

        assertThat(outboundPricing.vendorId(), is(VENDOR_X));
        assertThat(outboundPricing.instrumentId(), is(instrument));
        assertThat(outboundPricing.price(), is(TEN));

        List<OutboundPricing> vendorPricingTimelines = with().port(serverPortNumber).and().basePath(BASE_PATH)
                .get("/instrument/" + instrument)
                .then().log().body().and().extract().response().as(PRICING_LIST);

        assertThat(vendorPricingTimelines, hasSize(2));
        assertThat(vendorPricingTimelines.stream().map(OutboundPricing::vendorId).collect(toList()), containsInAnyOrder(VENDOR_X, VENDOR_Y));
        assertThat(vendorPricingTimelines.stream().map(OutboundPricing::ticker).collect(toList()), containsInAnyOrder("AAA.A", "BBB.B"));
        assertThat(vendorPricingTimelines.stream().map(OutboundPricing::price).collect(toList()), containsInAnyOrder(TEN, TWENTY));
        assertThat(vendorPricingTimelines.stream().map(OutboundPricing::priceDateTime).collect(toList()), containsInAnyOrder(NOW, NOW));
    }

    private OutboundPricing toOutboundPricing(Message message) throws Exception {
        String messageBody = ((ActiveMQTextMessage) message).getText();
        return objectMapper.readValue(messageBody, OutboundPricing.class);
    }

    private void registerNewPricing(InboundPricing inboundPricing) {
        with().port(serverPortNumber).and().basePath(BASE_PATH).and()
                .body(inboundPricing).with().contentType(JSON).log().body().and().post();
    }

}
