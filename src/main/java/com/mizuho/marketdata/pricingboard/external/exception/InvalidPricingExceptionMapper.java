
package com.mizuho.marketdata.pricingboard.external.exception;

import com.mizuho.marketdata.pricingboard.pricing.exception.InvalidPricingException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static java.util.stream.Collectors.joining;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Produces(MediaType.APPLICATION_JSON)
public class InvalidPricingExceptionMapper implements ExceptionMapper<InvalidPricingException> {
    @Override
    public Response toResponse(InvalidPricingException exception) {
        String invalidityDetails = exception.reasons().stream().collect(joining(", "));
        return Response
                .status(BAD_REQUEST)
                .entity(new ErrorResult(exception.getMessage(), invalidityDetails))
                .build();
    }
}