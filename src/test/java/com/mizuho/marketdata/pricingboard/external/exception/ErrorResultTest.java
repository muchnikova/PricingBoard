package com.mizuho.marketdata.pricingboard.external.exception;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ErrorResultTest {
    private static final java.lang.String MESSAGE = "message";
    private static final String DETAILS = "details";

    @Test
    public void should_create_well_formed_error_result() {
        ErrorResult errorResult = new ErrorResult(MESSAGE, DETAILS);

        assertThat(errorResult.message(), is(MESSAGE));
        assertThat(errorResult.details(), is(DETAILS));
    }

    @Test
    public void should_advise_equality_when_compared_to_idential_error_result() {
        assertThat(new ErrorResult(MESSAGE, DETAILS), is(equalTo(new ErrorResult(MESSAGE, DETAILS))));
    }

    @Test
    public void should_advise_inequality_when_compared_to_different_error_result() {
        assertThat(new ErrorResult(MESSAGE, DETAILS), is(not(equalTo(new ErrorResult(MESSAGE, "other")))));
    }
}