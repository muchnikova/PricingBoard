package com.mizuho.marketdata.pricingboard.external;

import com.mizuho.marketdata.pricingboard.pricing.InstrumentId;
import com.mizuho.marketdata.pricingboard.pricing.Pricing;
import com.mizuho.marketdata.pricingboard.pricing.VendorId;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.mizuho.marketdata.pricingboard.external.InboundPricing.Builder.anInboundPricing;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class InboundPricingTest {

    private static final BigDecimal TEN = new BigDecimal("10");
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final InboundPricing INBOUND_PRICING = anInboundPricing()
            .forInstrument("1").forVendor("1").forTicker("AAA.A").withPrice(TEN).withPriceDateTime(NOW).build();

    @Test
    public void should_construct_create_command() {
        assertThat(INBOUND_PRICING.instrumentId(), is("1"));
        assertThat(INBOUND_PRICING.vendorId(), is("1"));
        assertThat(INBOUND_PRICING.ticker(), is("AAA.A"));
        assertThat(INBOUND_PRICING.price(), is(TEN));
        assertThat(INBOUND_PRICING.priceDateTime(), is(NOW));
    }

    @Test
    public void should_copy_inbound_pricing() {
        InboundPricing anotherInboundPricing = INBOUND_PRICING.copy().build();
        assertThat(INBOUND_PRICING, is(anotherInboundPricing));
    }

    @Test
    public void should_recognise_equal_register_command() {
        InboundPricing anotherInboundPricing =
                anInboundPricing()
                        .forInstrument("1").forVendor("1").forTicker("AAA.A").withPrice(TEN).withPriceDateTime(NOW).build();

        assertThat(INBOUND_PRICING, is(anotherInboundPricing));
    }

    @Test
    public void should_covert_to_pricing(){
        Pricing pricing = INBOUND_PRICING.toPricing();
        assertThat(pricing.instrumentId(), is(new InstrumentId("1")));
        assertThat(pricing.vendorId(), is(new VendorId("1")));
        assertThat(pricing.ticker(), is("AAA.A"));
        assertThat(pricing.price(), is(TEN));
        assertThat(pricing.priceDateTime(), is(NOW));
    }

    @Test
    public void should_recognise_notequal_register_command_for_instrument() {
        InboundPricing anotherInboundPricing = INBOUND_PRICING.copy().forInstrument("2").build();
        assertThat(INBOUND_PRICING, is(not(equalTo(anotherInboundPricing))));
    }

    @Test
    public void should_recognise_notequal_register_command_for_vendor() {
        InboundPricing anotherInboundPricing = INBOUND_PRICING.copy().forVendor("2").build();
        assertThat(INBOUND_PRICING, is(not(equalTo(anotherInboundPricing))));
    }

    @Test
    public void should_recognise_notequal_register_command_for_ticker() {
        InboundPricing anotherInboundPricing = INBOUND_PRICING.copy().forTicker("BBB.B").build();
        assertThat(INBOUND_PRICING, is(not(equalTo(anotherInboundPricing))));
    }

    @Test
    public void should_recognise_notequal_register_command_for_price() {
        InboundPricing anotherInboundPricing = INBOUND_PRICING.copy().withPrice(BigDecimal.ONE).build();
        assertThat(INBOUND_PRICING, is(not(equalTo(anotherInboundPricing))));
    }

    @Test
    public void should_recognise_notequal_register_command_for_priceDate() {
        InboundPricing anotherInboundPricing = INBOUND_PRICING.copy().withPriceDateTime(NOW.minusDays(10)).build();
        assertThat(INBOUND_PRICING, is(not(equalTo(anotherInboundPricing))));
    }
}