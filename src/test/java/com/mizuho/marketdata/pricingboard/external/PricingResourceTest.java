package com.mizuho.marketdata.pricingboard.external;

import com.mizuho.marketdata.pricingboard.external.exception.ErrorResult;
import com.mizuho.marketdata.pricingboard.pricing.Pricing;
import com.mizuho.marketdata.pricingboard.pricing.PricingEnricher;
import com.mizuho.marketdata.pricingboard.pricing.PricingManagementService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static com.mizuho.marketdata.pricingboard.external.InboundPricing.Builder.anInboundPricing;
import static java.time.LocalDateTime.now;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PricingResourceTest {
    private static final BigDecimal TWENTY = new BigDecimal(20);
    private static final InboundPricing INBOUND_PRICING = anInboundPricing()
            .forInstrument("1").forVendor("V1").forTicker("AAA.A").withPrice(TWENTY).withPriceDateTime(now()).build();
    private static final Pricing PRICING = INBOUND_PRICING.toPricing();

    @Mock
    private PricingManagementService pricingManagementServiceService;
    @Mock
    private PricingEnricher pricingEnricher;
    @InjectMocks
    private PricingResource pricingResource;

    @Before
    public void setupPricingFactory() {
        when(pricingEnricher.enrich(PRICING)).thenReturn(PRICING);
    }

    @Test
    public void should_report_http_bad_request_when_inbound_pricing_is_missing() {
        Response response = pricingResource.registerPricing(null);

        assertThat(response.getStatus(), is(BAD_REQUEST.getStatusCode()));
        assertThat(response.getEntity(), is(new ErrorResult("Missing pricing details")));
    }

    @Test
    public void should_delegate_to_service_and_report_http_ok_when_registering_pricing() {
        Response response = pricingResource.registerPricing(INBOUND_PRICING);

        verify(pricingManagementServiceService).registerPricing(INBOUND_PRICING.toPricing());
        assertThat(response.getStatus(), is(OK.getStatusCode()));
    }

    @Test
    public void should_get_pricinginfo_for_vendorId() {
        Response response = pricingResource.getPricingForVendorId("V1");
        assertThat(response.getStatus(), is(OK.getStatusCode()));

    }

}
