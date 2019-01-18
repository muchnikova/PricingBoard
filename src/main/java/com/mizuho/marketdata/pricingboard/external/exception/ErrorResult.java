package com.mizuho.marketdata.pricingboard.external.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

public class ErrorResult {
    private String message;
    private String details;

    public ErrorResult(@JsonProperty("message") String message) {
        this.message = message;
    }

    public ErrorResult(@JsonProperty("message") String message, @JsonProperty("details") String details) {
        this.message = message;
        this.details = details;
    }

    @JsonProperty("message")
    public String message() {
        return message;
    }

    @JsonProperty("details")
    public String details() {
        return details;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("message", message)
                .add("details", details)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(message, details);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (this.getClass() != other.getClass()) {
            return false;
        }

        final ErrorResult that = (ErrorResult) other;

        return Objects.equal(this.message, that.message)
                && Objects.equal(this.details, that.details);
    }
}
