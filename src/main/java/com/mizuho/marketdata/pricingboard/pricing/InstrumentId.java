package com.mizuho.marketdata.pricingboard.pricing;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;

public class InstrumentId {
    private String value;

    public InstrumentId(String id){
        this.value = id;
    }

    public String value(){
        return value;
    }

    public boolean isEmpty(){
        return value ==null || value.isEmpty();
    }

    public String toString(){
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstrumentId that = (InstrumentId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public List<String> validate() {
        List<String> errors = newArrayList();
        validateId(errors);
        return errors;
    }

    private void validateId(List<String> errors) {
        if (value == null) {
            errors.add("instrumentId must be provided");
        } else if (value.isEmpty()) {
            errors.add("instrumentId must not be blank");
        }
    }
}
