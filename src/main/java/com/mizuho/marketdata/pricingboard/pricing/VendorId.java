package com.mizuho.marketdata.pricingboard.pricing;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Lists.newArrayList;

public class VendorId {
    private String value;

    public VendorId(String id){
        this.value = id;
    }

    public String value(){
        return value;
    }

    public boolean isEmpty(){
        return value == null || value.isEmpty();
    }

    public String toString(){
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VendorId vendorId = (VendorId) o;
        return Objects.equals(value, vendorId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public List<String> validate(){
        List<String> errors = newArrayList();
        validateValue(errors);
        return errors;
    }

    private void validateValue(List<String> errors){
        if (value == null){
            errors.add("vendorId must be provided");
        } else if(value.isEmpty()) {
            errors.add("vendorId must not be blank");
        }
    }
}
