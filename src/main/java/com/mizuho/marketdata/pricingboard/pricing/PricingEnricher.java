package com.mizuho.marketdata.pricingboard.pricing;

import static java.util.Objects.requireNonNull;

public class PricingEnricher {
    private PricingIdGenerator pricingIdGenerator;

    public PricingEnricher(PricingIdGenerator pricingIdGenerator) {
        this.pricingIdGenerator = requireNonNull(pricingIdGenerator, "pricingIdGenerator must not be null");
    }

    public Pricing enrich(Pricing pricing) {
        return pricing.copy().withId(pricingIdGenerator.generateId()).build();
    }
}
