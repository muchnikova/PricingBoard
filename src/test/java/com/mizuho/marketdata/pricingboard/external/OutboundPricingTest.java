package com.mizuho.marketdata.pricingboard.external;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.mizuho.marketdata.pricingboard.external.OutboundPricing.Builder.anOutboundPricing;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;


public class OutboundPricingTest {

    private static final BigDecimal TEN = new BigDecimal("10");
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final OutboundPricing OUTBOUND_PRICING = anOutboundPricing()
                    .withInstrument("1").withVendor("1").withTicker("AAA.A").withPrice(TEN).withPriceDateTime(NOW).build();

    @Test
    public void should_construct_create_command() {
        assertThat(OUTBOUND_PRICING.instrumentId(), is("1"));
        assertThat(OUTBOUND_PRICING.vendorId(), is("1"));
        assertThat(OUTBOUND_PRICING.ticker(), is("AAA.A"));
        assertThat(OUTBOUND_PRICING.price(), is(TEN));
        assertThat(OUTBOUND_PRICING.priceDateTime(), is(NOW));
    }

    @Test
    public void should_copy_inbound_pricing(){
        OutboundPricing anotherOutboundPricing = OUTBOUND_PRICING.copy().build();
        assertThat(OUTBOUND_PRICING, is(anotherOutboundPricing));
    }

    @Test
    public void should_recognise_equal_register_command() {
        OutboundPricing anotherOnboundPricing =
                anOutboundPricing()
                        .withInstrument("1").withVendor("1").withTicker("AAA.A").withPrice(TEN).withPriceDateTime(NOW).build();

        assertThat(OUTBOUND_PRICING, is(anotherOnboundPricing));
    }

    @Test
    public void should_recognise_notequal_register_command_for_instrument() {
        OutboundPricing anotherOnboundPricing = OUTBOUND_PRICING.copy().withInstrument("2").build();
        assertThat(OUTBOUND_PRICING, is(not(equalTo(anotherOnboundPricing))));
    }

    @Test
    public void should_recognise_notequal_register_command_for_vendor() {
        OutboundPricing anotherOnboundPricing = OUTBOUND_PRICING.copy().withVendor("2").build();
        assertThat(OUTBOUND_PRICING, is(not(equalTo(anotherOnboundPricing))));
    }

    @Test
    public void should_recognise_notequal_register_command_for_ticker() {
        OutboundPricing anotherOnboundPricing = OUTBOUND_PRICING.copy().withTicker("BBB.B").build();
        assertThat(OUTBOUND_PRICING, is(not(equalTo(anotherOnboundPricing))));
    }

    @Test
    public void should_recognise_notequal_register_command_for_price() {
        OutboundPricing anotherOnboundPricing = OUTBOUND_PRICING.copy().withPrice(BigDecimal.ONE).build();
        assertThat(OUTBOUND_PRICING, is(not(equalTo(anotherOnboundPricing))));
    }

    @Test
    public void should_recognise_notequal_register_command_for_priceDate() {
        OutboundPricing anotherOnboundPricing = OUTBOUND_PRICING.copy().withPriceDateTime(NOW.minusDays(10)).build();
        assertThat(OUTBOUND_PRICING, is(not(equalTo(anotherOnboundPricing))));
    }
}