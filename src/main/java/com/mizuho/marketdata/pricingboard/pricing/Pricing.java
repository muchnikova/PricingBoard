package com.mizuho.marketdata.pricingboard.pricing;

import com.google.common.base.Objects;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;

public final class Pricing {
    private PricingId id;
    private InstrumentId instrumentId;
    private VendorId vendorId;
    private String ticker;
    private BigDecimal price;
    private LocalDateTime priceDateTime;

    private Pricing(PricingId id, InstrumentId instrumentId, VendorId vendorId, String ticker, BigDecimal price, LocalDateTime priceDateTime) {
        this.id = id;
        this.instrumentId = instrumentId;
        this.vendorId = vendorId;
        this.ticker = ticker;
        this.price = price;
        this.priceDateTime  = priceDateTime;
    }

    public PricingId id() {return id;}
    public InstrumentId instrumentId() {
        return instrumentId;
    }
    public VendorId vendorId() { return vendorId; }
    public String ticker() { return ticker; }
    public BigDecimal price() {
        return price;
    }
    public LocalDateTime priceDateTime() { return priceDateTime; }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", id)
                .add("instrumentId", instrumentId)
                .add("vendorId", vendorId)
                .add("ticker", ticker)
                .add("price", price)
                .add("priceDateTime", priceDateTime)
                .toString();
    }

    public List<String> validate() {
        List<String> errors = newArrayList();

        validateId(errors);
        validateInstrumentId(errors);
        validateVendorId(errors);
        validateTicker(errors);
        validatePrice(errors);
        validatePriceDateTime(errors);

        return errors;
    }

    private void validateId(List<String> errors) {
        if (id == null) {
            errors.add("Id must be provided");
        } else if (id.isEmpty()) {
            errors.add("Id must not be blank");
        }
    }

    private void validateInstrumentId(List<String> errors) {
        if (instrumentId == null) {
            errors.add("instrumentId must be provided");
        } else if (instrumentId.isEmpty()) {
            errors.add("instrumentId must not be blank");
        }
    }

    private void validateVendorId(List<String> errors) {
        if (vendorId == null) {
            errors.add("vendorId must be provided");
        } else if (vendorId.isEmpty()) {
            errors.add("vendorId must not be blank");
        }
    }

    private void validateTicker(List<String> errors) {
        if (ticker == null) {
            errors.add("ticker must be provided");
        } else if (ticker.isEmpty()) {
            errors.add("ticker must not be blank");
        }
    }

    private void validatePriceDateTime(List<String> errors) {
        if (priceDateTime == null) {
            errors.add("priceDateTime must be provided");
        }
    }

    private void validatePrice(List<String> errors) {
        if (price == null) {
            errors.add("price must be provided");
        } else if (price.compareTo(ZERO) <= 0) {
            errors.add(format("price must be a positive number while %s was provided", price));
        }
    }

    public Pricing.Builder copy() {
        return Builder.aPricing().withId(id).forInstrument(instrumentId).forVendor(vendorId)
                .forTicker(ticker).withPrice(price).withPriceDateTime(priceDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, instrumentId, vendorId, ticker, price, priceDateTime);
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

        final Pricing that = (Pricing) other;

        return Objects.equal(this.id, that.id)
                && Objects.equal(this.instrumentId, that.instrumentId)
                && Objects.equal(this.vendorId, that.vendorId)
                && Objects.equal(this.ticker, that.ticker)
                && Objects.equal(this.price, that.price)
                && Objects.equal(this.priceDateTime, that.priceDateTime);
    }

    public static class Builder {
        private PricingId id;
        private InstrumentId instrumentId;
        private VendorId vendorId;
        private String ticker;
        private BigDecimal price;
        private LocalDateTime priceDateTime;

        private Builder() {
        }

        public static Builder aPricing() {
            return new Builder();
        }

        public Builder withId(PricingId id) {
            this.id = id;
            return this;
        }

        public Builder forInstrument(InstrumentId instrumentId) {
            this.instrumentId = instrumentId;
            return this;
        }

        public Builder forVendor(VendorId vendorId) {
            this.vendorId= vendorId;
            return this;
        }

        public Builder forTicker(String ticker) {
            this.ticker = ticker;
            return this;
        }

        public Builder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }
        public Builder withPriceDateTime(LocalDateTime priceDateTime) {
            this.priceDateTime = priceDateTime;
            return this;
        }

        public Pricing build() {
            return new Pricing(id, instrumentId, vendorId, ticker, price, priceDateTime);
        }
    }
}
