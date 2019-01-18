package com.mizuho.marketdata.pricingboard.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;
import com.mizuho.marketdata.pricingboard.pricing.Pricing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.google.common.base.MoreObjects.toStringHelper;

@JsonDeserialize(builder = OutboundPricing.Builder.class)
public final class OutboundPricing {
    private String instrumentId;
    private String vendorId;
    private String ticker;
    private BigDecimal price;
    private LocalDateTime priceDateTime;

    public static OutboundPricing fromPricing(Pricing pricing) {
        return new OutboundPricing(
                pricing.instrumentId().toString(), pricing.vendorId().toString(), pricing.ticker(), pricing.price(), pricing.priceDateTime());
    }

    private OutboundPricing(String instrumentId, String vendorId, String ticker, BigDecimal price, LocalDateTime priceDateTime) {
        this.instrumentId = instrumentId;
        this.vendorId = vendorId;
        this.ticker = ticker;
        this.price = price;
        this.priceDateTime = priceDateTime;
    }

    public OutboundPricing.Builder copy(){
        return Builder.anOutboundPricing().withInstrument(instrumentId).withVendor(vendorId).withTicker(ticker)
                .withPrice(price).withPriceDateTime(priceDateTime);
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

        final OutboundPricing that = (OutboundPricing) other;

        return Objects.equal(this.vendorId, that.vendorId)
                && Objects.equal(this.instrumentId, that.instrumentId)
                && Objects.equal(this.ticker, that.ticker)
                && Objects.equal(this.price, that.price)
                && Objects.equal(this.priceDateTime, that.priceDateTime);
    }

    public static class Builder {
        private String instrumentId;
        private String vendorId;
        private String ticker;
        private BigDecimal price;
        private LocalDateTime priceDateTime;

        private Builder() {
        }

        public static Builder anOutboundPricing() {
            return new Builder();
        }

        @JsonProperty("instrumentId")
        public Builder withInstrument(String instrumentId) {
            this.instrumentId = instrumentId;
            return this;
        }

        @JsonProperty("vendorId")
        public Builder withVendor(String vendorId) {
            this.vendorId= vendorId;
            return this;
        }

        public Builder withTicker(String ticker) {
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

        public OutboundPricing build() {
            return new OutboundPricing(instrumentId, vendorId, ticker, price, priceDateTime);
        }
    }
}
