package com.mizuho.marketdata.pricingboard.pricing;

import java.util.function.Supplier;

public interface PricingIdGenerator extends Supplier<PricingId> {
    PricingId generateId();

    default PricingId get() {
        return generateId();
    }
}
