package com.mizuho.marketdata.pricingboard.pricing.exception;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class InvalidPricingException extends RuntimeException {
    private List<String> reasons;

    public InvalidPricingException(String message, List<String> reasons) {
        super(message);
        this.reasons = requireNonNull(reasons, "reasons must not be null");
    }
    public List<String> reasons() {
        return reasons;
    }
}