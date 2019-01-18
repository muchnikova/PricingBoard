package com.mizuho.marketdata.pricingboard.pricing;

import org.junit.Assert;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;


public class InstrumentIdTest {

    private InstrumentId instrumentId = new InstrumentId("1");

    @Test
    public void should_construct_instrument_id_object(){
        assertThat(instrumentId.value(), is("1"));
    }

    @Test
    public void should_advise_instrument_id_is_empty_then_value_is_null(){
        InstrumentId instrumentId = new InstrumentId(null);
        assertThat(true, is(instrumentId.isEmpty()));

    }

    @Test
    public void should_advise_instrument_id_is_empty_then_value_is_blank(){
        InstrumentId instrumentId = new InstrumentId("");
        assertThat(true, is(instrumentId.isEmpty()));

    }

    @Test
    public void should_advise_equality_when_compared_to_idential_instrument_ids() {
        InstrumentId anotherInstrumentId = new InstrumentId("1");
        assertThat(instrumentId, is(anotherInstrumentId));

    }

    @Test
    public void should_advise_inequality_when_compared_to_different_instrument_ids() {
       InstrumentId anotherInstrumentId = new InstrumentId("2");
        assertThat(instrumentId, is(not(anotherInstrumentId)));

    }

    @Test
    public void should_report_no_validation_errors_on_valid_instrument_id() {
        Assert.assertThat(instrumentId.validate(), is(emptyList()));
    }

    @Test
    public void should_report_validation_errors_on_null_value() {
        InstrumentId instrumentId = new InstrumentId(null);
        assertThat(instrumentId.validate(), is(singletonList("instrumentId must be provided")));
    }

    @Test
    public void should_report_validation_errors_on_empty_value() {
        InstrumentId instrumentId = new InstrumentId("");
        assertThat(instrumentId.validate(), is(singletonList("instrumentId must not be blank")));
    }


}
