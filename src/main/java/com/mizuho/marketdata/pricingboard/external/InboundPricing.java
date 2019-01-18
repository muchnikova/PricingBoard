package com.mizuho.marketdata.pricingboard.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;
import com.mizuho.marketdata.pricingboard.pricing.InstrumentId;
import com.mizuho.marketdata.pricingboard.pricing.Pricing;
import com.mizuho.marketdata.pricingboard.pricing.VendorId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.google.common.base.MoreObjects.toStringHelper;

@JsonDeserialize(builder = InboundPricing.Builder.class)
public final class InboundPricing {
    private String instrumentId;
    private String vendorId;
    private String ticker;
    private BigDecimal price;
    private LocalDateTime priceDateTime;

    private InboundPricing(String instrumentId, String vendorId, String ticker, BigDecimal price, LocalDateTime priceDateTime) {
        this.instrumentId = instrumentId;
        this.vendorId = vendorId;
        this.ticker = ticker;
        this.price = price;
        this.priceDateTime = priceDateTime;
    }

    @JsonProperty("instrumentId")
    public String instrumentId() {
        return instrumentId;
    }

    @JsonProperty("vendorId")
    public String vendorId() { return vendorId; }

    @JsonProperty("ticker")
    public String ticker() { return ticker; }

    @JsonProperty("price")
    public BigDecimal price() {
        return price;
    }

    @JsonProperty("priceDateTime")
    public LocalDateTime priceDateTime() { return priceDateTime; }

    public Pricing toPricing() {
        return Pricing.Builder.aPricing()
                .forInstrument(new InstrumentId(instrumentId))
                .forVendor(new VendorId(vendorId))
                .forTicker(ticker)
                .withPrice(price)
                .withPriceDateTime(priceDateTime)
                .build();
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("instrumentId", instrumentId)
                .add("vendorId", vendorId)
                .add("ticker", ticker)
                .add("price", price)
                .add("priceDateTime", priceDateTime)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(instrumentId, vendorId, ticker, price, priceDateTime);
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

        final InboundPricing that = (InboundPricing) other;

        return Objects.equal(this.vendorId, that.vendorId)
                && Objects.equal(this.instrumentId, that.instrumentId)
                && Objects.equal(this.ticker, that.ticker)
                && Objects.equal(this.price, that.price)
                && Objects.equal(this.priceDateTime, that.priceDateTime);
    }

    public InboundPricing.Builder copy(){
        return Builder.anInboundPricing().forInstrument(instrumentId).forVendor(vendorId).forTicker(ticker).withPrice(price).withPriceDateTime(priceDateTime);
    }

    public static class Builder {
        private String instrumentId;
        private String vendorId;
        private String ticker;
        private BigDecimal price;
        private LocalDateTime priceDateTime;

        private Builder() {
        }

        public static Builder anInboundPricing() {
            return new Builder();
        }

        @JsonProperty("instrumentId")
        public Builder forInstrument(String instrumentId) {
            this.instrumentId = instrumentId;
            return this;
        }

        @JsonProperty("vendorId")
        public Builder forVendor(String vendorId) {
            this.vendorId= vendorId;
            return this;
        }

        @JsonProperty("ticker")
        public Builder forTicker(String ticker) {
            this.ticker = ticker;
            return this;
        }

        @JsonProperty("price")
        public Builder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        @JsonProperty("priceDateTime")
        public Builder withPriceDateTime(LocalDateTime priceDateTime) {
            this.priceDateTime = priceDateTime;
            return this;
        }

        public InboundPricing build() {
            return new InboundPricing(instrumentId, vendorId, ticker, price, priceDateTime);
        }
    }
}
