package com.mizuho.marketdata.pricingboard.pricing;

import java.util.Collection;

public interface PricingRepository {

    void store(Pricing pricing);

    Collection<Pricing> allByInstrument(InstrumentId instrumentId);

    Collection<Pricing> allByVendor(VendorId vendorId);

}
