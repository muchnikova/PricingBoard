package com.mizuho.marketdata.pricingboard.external.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

@Produces(MediaType.APPLICATION_JSON)
public class CatchAllExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger LOG = LoggerFactory.getLogger(CatchAllExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        LOG.error("Unhandled exception!", exception);
        return Response
                .status(INTERNAL_SERVER_ERROR)
                .entity(new ErrorResult("Sorry, something is broken. We'll look into it.")).build();
    }
}
