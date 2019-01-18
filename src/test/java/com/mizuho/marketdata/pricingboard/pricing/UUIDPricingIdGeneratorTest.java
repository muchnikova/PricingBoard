package com.mizuho.marketdata.pricingboard.pricing;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class UUIDPricingIdGeneratorTest {
    @Test
    public void should_generate_random_unique_pricing_id() {
        UUIDPricingIdGenerator pricingIdGenerator = new UUIDPricingIdGenerator();

        assertThat(pricingIdGenerator.generateId(), is(not(equalTo(pricingIdGenerator.generateId()))));
    }
}