package com.mizuho.marketdata.pricingboard.infrastructure;

import com.mizuho.marketdata.pricingboard.pricing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

public class InMemoryPricingRepository implements PricingRepository {
    private static final Logger LOG = LoggerFactory.getLogger(PricingManagementService.class);
    private static final int PRICE_DATE_LIVE_PERIOD = 30;
    private static final LocalDate PRICE_DATE_CUTOFF = LocalDate.now().minusDays(PRICE_DATE_LIVE_PERIOD);

    private Map<InstrumentId, Map<VendorId, Pricing>> indexByInstrument = new ConcurrentHashMap<>();
    private Map<VendorId, Map<InstrumentId, Pricing>> indexByVendor = new ConcurrentHashMap<>();
    private Map<LocalDate, Set<Pricing>> indexByPriceDate = new ConcurrentHashMap<>();

    public void evictEligible() {
        LOG.info(format("Evicting pricing data older than %s days", PRICE_DATE_LIVE_PERIOD));
        Set<LocalDate> dateBucketsToEvict = indexByPriceDate.keySet().stream()
                .filter(i -> i.isBefore(PRICE_DATE_CUTOFF))
                .collect(Collectors.toSet());

        dateBucketsToEvict.forEach(dateBucket -> indexByPriceDate.remove(dateBucket).forEach(this::evictPricing));
    }

    private void evictPricing(Pricing pricing) {
        Map<VendorId, Pricing> instrumentPricings = indexByInstrument.getOrDefault(pricing.instrumentId(), emptyMap());
        if (pricing.equals(instrumentPricings.get(pricing.vendorId()))) {
            instrumentPricings.remove(pricing.vendorId());
        }
        Map<InstrumentId, Pricing> vendorPricings = indexByVendor.getOrDefault(pricing.vendorId(), emptyMap());
        if (pricing.equals(vendorPricings.get(pricing.instrumentId()))) {
            vendorPricings.remove(pricing.instrumentId());
        }
    }

    @Override
    public void store(Pricing pricing) {
        Pricing previouslyLatestByInstrument =
                indexByInstrument.computeIfAbsent(pricing.instrumentId(), i -> new ConcurrentHashMap<>()).put(pricing.vendorId(), pricing);

        Pricing previouslyLatestByVendor =
                indexByVendor.computeIfAbsent(pricing.vendorId(), i -> new ConcurrentHashMap<>()).put(pricing.instrumentId(), pricing);

        indexByPriceDate.computeIfAbsent(pricing.priceDateTime().toLocalDate(), d -> ConcurrentHashMap.newKeySet()).add(pricing);

        if (previouslyLatestByInstrument != null) {
            indexByPriceDate.getOrDefault(previouslyLatestByInstrument.priceDateTime().toLocalDate(), emptySet()).remove(previouslyLatestByInstrument);
        }
        if (previouslyLatestByVendor != null) {
            indexByPriceDate.getOrDefault(previouslyLatestByVendor.priceDateTime().toLocalDate(), emptySet()).remove(previouslyLatestByVendor);
        }
    }

    public Collection<Pricing> allByInstrument(InstrumentId instrumentId) {
        return indexByInstrument.getOrDefault(instrumentId, emptyMap()).values();
    }

    public Collection<Pricing> allByVendor(VendorId vendorId) {
        return indexByVendor.getOrDefault(vendorId, emptyMap()).values();
    }

}
