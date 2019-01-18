package com.mizuho.marketdata.pricingboard.pricing;

import com.mizuho.marketdata.pricingboard.pricing.exception.InvalidPricingException;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.mizuho.marketdata.pricingboard.pricing.Pricing.Builder.aPricing;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PricingManagementServiceTest {
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final List<Pricing> PRICING_TIMELINE = newArrayList(
            aPricing().withId(new PricingId("1")).forInstrument(new InstrumentId("I1")).forVendor(new VendorId("V1")).forTicker("AAA.A").withPrice(TEN).withPriceDateTime(NOW).build(),
            aPricing().withId(new PricingId("2")).forInstrument(new InstrumentId("I2")).forVendor(new VendorId("V2")).forTicker("BBB.B").withPrice(TEN).withPriceDateTime(NOW).build());

    @Mock
    private PricingRepository pricingRepository;

    private PricingManagementService pricingManagementService;

    @Before
    public void setup_order_management_service() {
        pricingManagementService = new PricingManagementService(pricingRepository);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void should_deny_invalid_pricing_registration() {
        exception.expect(InvalidPricingException.class);
        exception.expectMessage("Pricing registration failed");
        exception.expect(reasons(equalTo(Arrays.asList("Id must be provided", "instrumentId must be provided", "price must be provided", "priceDateTime must be provided"))));

        Pricing invalidPricing = aPricing().forVendor(new VendorId("1")).forTicker("AAA.A").build();
        pricingManagementService.registerPricing(invalidPricing);
    }


    @Test
    public void should_store_valid_pricing() {

        pricingManagementService.registerPricing(PRICING_TIMELINE.get(0));

        verify(pricingRepository).store(PRICING_TIMELINE.get(0));
    }


    @Test
    public void should_return_vendor_pricing_timeline_for_instrument() {
        InstrumentId instrumentId = new InstrumentId("I1");

        when(pricingRepository.allByInstrument(instrumentId)).thenReturn(PRICING_TIMELINE);

        assertThat(pricingManagementService.allByInstument(instrumentId), is(PRICING_TIMELINE));
    }

    @Test
    public void should_return_instrument_pricing_timeline_for_vendor() {
        VendorId vendorId = new VendorId("V1");

        when(pricingRepository.allByVendor(vendorId)).thenReturn(PRICING_TIMELINE);

        assertThat(pricingManagementService.allByVendor(vendorId), is(PRICING_TIMELINE));
    }

    private static FeatureMatcher<InvalidPricingException, List<String>> reasons(Matcher<List<String>> matcher) {
        return new FeatureMatcher<InvalidPricingException, List<String>>(matcher, " exception reasons", "reasons") {
            @Override
            protected List<String> featureValueOf(InvalidPricingException exception) {
                return exception.reasons();
            }
        };
    }
}