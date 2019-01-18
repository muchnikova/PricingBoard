package com.mizuho.marketdata.pricingboard.pricing;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.mizuho.marketdata.pricingboard.pricing.Pricing.Builder.aPricing;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PricingEnricherTest {
    private static final BigDecimal TEN = new BigDecimal("10");
    private static final LocalDate TODAY = LocalDate.now();

    @Mock
    private PricingIdGenerator pricingIdGenerator;

    @InjectMocks
    private PricingEnricher pricingEnricher;

    @Before
    public void setupIdGenerator() {
        when(pricingIdGenerator.generateId()).thenReturn(new PricingId("id"));
    }

    @Test
    public void should_enrich_pricing_with_id() {
        Pricing pricing = aPricing().build();

        assertThat(pricingEnricher.enrich(pricing), is(aPricing().withId(new PricingId("id")).build()));
    }
}