package com.mizuho.marketdata.pricingboard.external.exception;

import com.mizuho.marketdata.pricingboard.pricing.exception.InvalidPricingException;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class InvalidPricingExceptionMapperTest {
    @Test
    public void should_map_invalid_order_details_into_http_bad_request_response() {
        Response response = new InvalidPricingExceptionMapper().toResponse(
                new InvalidPricingException("Failure message", asList("reasonOne", "reasonTwo")));

        assertThat(response.getStatus(), is(BAD_REQUEST.getStatusCode()));
        assertThat(response.getEntity(), Matchers.is(
                new ErrorResult("Failure message", "reasonOne, reasonTwo")));
    }
}