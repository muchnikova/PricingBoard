package com.mizuho.marketdata.pricingboard.pricing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.UUID.randomUUID;

public class UUIDPricingIdGenerator implements PricingIdGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(UUIDPricingIdGenerator.class);

    @Override
    public PricingId generateId() {
        String generatedPricingId = randomUUID().toString();

        LOG.debug("Generated new order id {}", generatedPricingId);
        return new PricingId(generatedPricingId);
    }
}
