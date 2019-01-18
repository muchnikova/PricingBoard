package com.mizuho.marketdata.pricingboard.pricing;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.mizuho.marketdata.pricingboard.pricing.Pricing.Builder.aPricing;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

public class PricingTest {
    private static final BigDecimal TEN = new BigDecimal(10);
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final Pricing PRICING = aPricing().withId(new PricingId("1"))
                    .forInstrument(new InstrumentId("1"))
                    .forVendor(new VendorId("1"))
                    .forTicker("AAA.A")
                    .withPrice(TEN)
                    .withPriceDateTime(NOW).build();

    private static final String BLANK = "";

    @Test
    public void should_construct_pricing_object() {
        assertThat(PRICING.id(), is(new PricingId("1")));
        assertThat(PRICING.instrumentId(), is(new InstrumentId("1")));
        assertThat(PRICING.vendorId(), is(new VendorId("1")));
        assertThat(PRICING.ticker(), is("AAA.A"));
        assertThat(PRICING.price(), is(TEN));
        assertThat(PRICING.priceDateTime(), is(NOW));
    }

    @Test
    public void should_copy_pricing_into_identical_pricing() {
        assertThat(PRICING.copy().build(), is(equalTo(PRICING)));
    }


    @Test
    public void should_advise_equality_when_compared_to_idential_pricing_objects() {
        Pricing anotherPricing = aPricing().withId(new PricingId("1"))
                .forInstrument(new InstrumentId("1"))
                .forVendor(new VendorId("1"))
                .forTicker("AAA.A")
                .withPrice(TEN)
                .withPriceDateTime(NOW).build();
        assertThat(PRICING, is(equalTo(anotherPricing)));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_pricing_objects_by_pricing_id() {
        Pricing anotherPricing = PRICING.copy().withId(new PricingId("2")).build();
        assertThat(PRICING, is(not(equalTo(anotherPricing))));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_pricing_objects_by_instrument_id() {
        Pricing anotherPricing = PRICING.copy().forInstrument(new InstrumentId("2")).build();
        assertThat(PRICING, is(not(equalTo(anotherPricing))));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_pricing_objects_by_vendor_id() {
        Pricing anotherPricing = PRICING.copy().forVendor(new VendorId("2")).build();
        assertThat(PRICING, is(not(equalTo(anotherPricing))));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_pricing_objects_by_ticker() {
        Pricing anotherPricing = PRICING.copy().forTicker("BBB.B").build();
        assertThat(PRICING, is(not(equalTo(anotherPricing))));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_pricing_objects_by_price() {
        Pricing anotherPricing = PRICING.copy().withPrice(BigDecimal.ONE).build();
        assertThat(PRICING, is(not(equalTo(anotherPricing))));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_pricing_objects_by_price_date() {
        Pricing anotherPricing = PRICING.copy().withPriceDateTime(NOW.minusDays(1)).build();
        assertThat(PRICING, is(not(equalTo(anotherPricing))));
    }

    @Test
    public void should_report_no_validation_errors_on_valid_pricing() {
        assertThat(PRICING.validate(), is(emptyList()));
    }

    @Test
    public void should_report_validation_error_when_id_not_provided() {
        Pricing invalidPricing = PRICING.copy().withId(null).build();

        assertThat(invalidPricing.validate(), is(singletonList("Id must be provided")));
    }

    @Test
    public void should_report_validation_error_when_id_is_blank() {
        Pricing invalidPricing = PRICING.copy().withId(new PricingId(BLANK)).build();

        assertThat(invalidPricing.validate(), is(singletonList("Id must not be blank")));
    }

    @Test
    public void should_report_validation_error_when_instrument_id_not_provided() {
        Pricing invalidPricing = PRICING.copy().forInstrument(null).build();

        assertThat(invalidPricing.validate(), is(singletonList("instrumentId must be provided")));
    }

    @Test
    public void should_report_validation_error_when_instrument_id_is_blank() {
        Pricing invalidPricing = PRICING.copy().forInstrument(new InstrumentId(BLANK)).build();

        assertThat(invalidPricing.validate(), is(singletonList("instrumentId must not be blank")));
    }

    @Test
    public void should_report_validation_error_when_vendor_id_not_provided() {
        Pricing invalidPricing = PRICING.copy().forVendor(null).build();

        assertThat(invalidPricing.validate(), is(singletonList("vendorId must be provided")));
    }

    @Test
    public void should_report_validation_error_when_vendor_id_blank() {
        Pricing invalidPricing = PRICING.copy().forVendor(new VendorId(BLANK)).build();

        assertThat(invalidPricing.validate(), is(singletonList("vendorId must not be blank")));
    }

    @Test
    public void should_report_validation_error_when_ticker_not_provided() {
        Pricing invalidPricing = PRICING.copy().forTicker(null).build();

        assertThat(invalidPricing.validate(), is(singletonList("ticker must be provided")));
    }

    @Test
    public void should_report_validation_error_when_ticker_is_blank() {
        Pricing invalidPricing = PRICING.copy().forTicker(BLANK).build();

        assertThat(invalidPricing.validate(), is(singletonList("ticker must not be blank")));
    }

    @Test
    public void should_report_validation_error_when_price_not_provided() {
        Pricing invalidPricing = PRICING.copy().withPrice(null).build();

        assertThat(invalidPricing.validate(), is(singletonList("price must be provided")));
    }

    @Test
    public void should_report_validation_error_when_price_is_zero() {
        Pricing invalidPricing = PRICING.copy().withPrice(BigDecimal.ZERO).build();

        assertThat(invalidPricing.validate(), is(singletonList(format("price must be a positive number while %s was provided", BigDecimal.ZERO))));
    }

    @Test
    public void should_report_validation_error_when_price_is_negative() {
        Pricing invalidPricing = PRICING.copy().withPrice(new BigDecimal(-10)).build();

        assertThat(invalidPricing.validate(), is(singletonList(format("price must be a positive number while %s was provided", new BigDecimal(-10)))));
    }

    @Test
    public void should_report_validation_error_when_price_date_id_not_provided() {
        Pricing invalidPricing = PRICING.copy().withPriceDateTime(null).build();

        assertThat(invalidPricing.validate(), is(singletonList("priceDateTime must be provided")));
    }
}