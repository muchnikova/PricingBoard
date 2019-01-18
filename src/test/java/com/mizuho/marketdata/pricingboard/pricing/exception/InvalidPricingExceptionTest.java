package com.mizuho.marketdata.pricingboard.pricing.exception;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InvalidPricingExceptionTest {
    @Test
    public void should_construct_well_formed_exception_object() {
        List<String> reasons = asList("reasonOne", "reasonTwo");
        InvalidPricingException exception = new InvalidPricingException("Failure", reasons);

        assertThat(exception.getMessage(), is("Failure"));
        assertThat(exception.reasons(), is(reasons));
    }
}