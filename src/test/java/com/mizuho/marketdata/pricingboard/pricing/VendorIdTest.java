package com.mizuho.marketdata.pricingboard.pricing;

import org.junit.Assert;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class VendorIdTest {
    private VendorId vendorId = new VendorId("1");

    @Test
    public void should_construnct_vendor_id_object(){
        assertThat(vendorId.value(), is("1"));
    }

    @Test
    public void should_advise_vendor_id_is_empty_then_value_is_null(){
        VendorId vendorId = new VendorId(null);
        assertThat(true, is(vendorId.isEmpty()));

    }

    @Test
    public void should_advise_vendor_id_is_empty_then_value_is_blank(){
        VendorId vendorId = new VendorId("");
        assertThat(true, is(vendorId.isEmpty()));

    }

    @Test
    public void should_advise_equality_when_compared_to_idential_vendor_ids() {
        VendorId anotherVendorId = new VendorId("1");
        assertThat(vendorId, is(anotherVendorId));

    }

    @Test
    public void should_advise_inequality_when_compared_to_different_vendor_ids() {
        VendorId anotherVendorId = new VendorId("2");
        assertThat(vendorId, is(not(anotherVendorId)));

    }

    @Test
    public void should_report_no_validation_errors_on_valid_vendor_id() {
        Assert.assertThat(vendorId.validate(), is(emptyList()));
    }

    @Test
    public void should_report_validation_errors_on_null_value() {
        VendorId vendorId = new VendorId(null);
        assertThat(vendorId.validate(), is(singletonList("vendorId must be provided")));
    }

    @Test
    public void should_report_validation_errors_on_empty_value() {
        VendorId vendorId = new VendorId("");
        assertThat(vendorId.validate(), is(singletonList("vendorId must not be blank")));
    }


}
