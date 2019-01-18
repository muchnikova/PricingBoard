package com.mizuho.marketdata.pricingboard.pricing;

import org.junit.Assert;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class PricingIdTest {
    private PricingId pricingId = new PricingId("1");

    @Test
    public void should_construnct_vendor_id_object(){
        assertThat(pricingId.value(), is("1"));
    }

    @Test
    public void should_advise_vendor_id_is_empty_then_value_is_null(){
        PricingId pricingId = new PricingId(null);
        assertThat(true, is(pricingId.isEmpty()));

    }

    @Test
    public void should_advise_pricing_id_is_empty_then_value_is_blank(){
        PricingId pricingId = new PricingId("");
        assertThat(true, is(pricingId.isEmpty()));

    }

    @Test
    public void should_advise_equality_when_compared_to_idential_pricing_ids() {
        PricingId anotherpricingId = new PricingId("1");
        assertThat(pricingId, is(anotherpricingId));

    }

    @Test
    public void should_advise_inequality_when_compared_to_different_pricing_ids() {
        PricingId anotherpricingId = new PricingId("2");
        assertThat(pricingId, is(not(anotherpricingId)));

    }

    @Test
    public void should_report_no_validation_errors_on_valid_pricing_id() {
        Assert.assertThat(pricingId.validate(), is(emptyList()));
    }

    @Test
    public void should_report_validation_errors_on_null_value() {
        PricingId pricingId = new PricingId(null);
        assertThat(pricingId.validate(), is(singletonList("pricingId must be provided")));
    }

    @Test
    public void should_report_validation_errors_on_empty_value() {
        PricingId pricingId = new PricingId("");
        assertThat(pricingId.validate(), is(singletonList("pricingId must not be blank")));
    }

}
