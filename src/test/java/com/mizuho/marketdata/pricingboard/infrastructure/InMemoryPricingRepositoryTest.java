package com.mizuho.marketdata.pricingboard.infrastructure;

import com.mizuho.marketdata.pricingboard.pricing.InstrumentId;
import com.mizuho.marketdata.pricingboard.pricing.Pricing;
import com.mizuho.marketdata.pricingboard.pricing.PricingId;
import com.mizuho.marketdata.pricingboard.pricing.VendorId;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.mizuho.marketdata.pricingboard.pricing.Pricing.Builder.aPricing;
import static java.math.BigDecimal.TEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;


public class InMemoryPricingRepositoryTest {

    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final List<Pricing> PRICINGS_LIST = newArrayList(
            aPricing().withId(new PricingId("1")).forInstrument(new InstrumentId("I1")).forVendor(new VendorId("V1")).forTicker("AAA.A").withPrice(TEN).withPriceDateTime(NOW).build(),
            aPricing().withId(new PricingId("2")).forInstrument(new InstrumentId("I2")).forVendor(new VendorId("V2")).forTicker("BBB.B").withPrice(TEN).withPriceDateTime(NOW).build(),
            aPricing().withId(new PricingId("3")).forInstrument(new InstrumentId("I1")).forVendor(new VendorId("V2")).forTicker("CCC.C").withPrice(TEN).withPriceDateTime(NOW).build(),
            aPricing().withId(new PricingId("4")).forInstrument(new InstrumentId("I2")).forVendor(new VendorId("V1")).forTicker("DDD.D").withPrice(TEN).withPriceDateTime(NOW).build());


    private static final List<Pricing> PRICINGS_LIST_OUTDATED = newArrayList(
            aPricing().withId(new PricingId("5")).forInstrument(new InstrumentId("I3")).forVendor(new VendorId("V3")).forTicker("AAA.A").withPrice(TEN).withPriceDateTime(NOW).build(),
            aPricing().withId(new PricingId("6")).forInstrument(new InstrumentId("I3")).forVendor(new VendorId("V4")).forTicker("BBB.B")
                    .withPrice(TEN).withPriceDateTime(NOW.minusDays(35)).build(),
            aPricing().withId(new PricingId("7")).forInstrument(new InstrumentId("I3")).forVendor(new VendorId("V5")).forTicker("DDD.D")
                    .withPrice(TEN).withPriceDateTime(NOW.minusDays(31)).build());

    private InMemoryPricingRepository inMemoryPricingRepository = new InMemoryPricingRepository();

    @Before
    public void store_pricing_list(){
            PRICINGS_LIST.stream().forEach(i -> inMemoryPricingRepository.store(i));
    }

    @Test
    public void should_return_vendor_pricings_by_instrument(){

        Collection<Pricing> vendorPricings = inMemoryPricingRepository.allByInstrument(new InstrumentId("I1"));
        assertThat(vendorPricings, hasSize(2));
        assertThat(vendorPricings.stream().collect(Collectors.toList()),
                containsInAnyOrder(
                        aPricing().withId(new PricingId("1")).forInstrument(new InstrumentId("I1")).forVendor(new VendorId("V1")).forTicker("AAA.A").withPrice(TEN).withPriceDateTime(NOW).build(),
                        aPricing().withId(new PricingId("3")).forInstrument(new InstrumentId("I1")).forVendor(new VendorId("V2")).forTicker("CCC.C").withPrice(TEN).withPriceDateTime(NOW).build()));
    }

    @Test
    public void should_return_instrument_pricing_timeline_by_vendor() {
        Collection<Pricing> instrumentPricings = inMemoryPricingRepository.allByVendor(new VendorId("V2"));
        assertThat(instrumentPricings, hasSize(2));
        assertThat(instrumentPricings.stream().collect(Collectors.toList()),
                containsInAnyOrder(
                        aPricing().withId(new PricingId("2")).forInstrument(new InstrumentId("I2")).forVendor(new VendorId("V2")).forTicker("BBB.B").withPrice(TEN).withPriceDateTime(NOW).build(),
                        aPricing().withId(new PricingId("3")).forInstrument(new InstrumentId("I1")).forVendor(new VendorId("V2")).forTicker("CCC.C").withPrice(TEN).withPriceDateTime(NOW).build()));

    }
    @Test
    public void should_evict_outdated_pricing_from_memory(){

        PRICINGS_LIST_OUTDATED.stream().forEach(i -> inMemoryPricingRepository.store(i));
        assertThat(inMemoryPricingRepository.allByInstrument(new InstrumentId("I3")), hasSize(3));
        inMemoryPricingRepository.evictEligible();
        System.out.println(inMemoryPricingRepository.allByInstrument(new InstrumentId("I3")));
        assertThat(inMemoryPricingRepository.allByInstrument(new InstrumentId("I3")), hasSize(1));

    }

}