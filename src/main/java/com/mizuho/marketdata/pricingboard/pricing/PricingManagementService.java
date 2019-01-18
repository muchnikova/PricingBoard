package com.mizuho.marketdata.pricingboard.pricing;

import com.mizuho.marketdata.pricingboard.pricing.exception.InvalidPricingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class PricingManagementService {
    private static final Logger LOG = LoggerFactory.getLogger(PricingManagementService.class);

    private PricingRepository pricingRepository;

    public PricingManagementService(PricingRepository pricingRepository) {
        this.pricingRepository = requireNonNull(pricingRepository, "pricingRepository must not be null");
   }

    public void registerPricing(Pricing targetPricing) {
        LOG.info("Registering new pricing with details: {}", targetPricing);

        List<String> errors = targetPricing.validate();
        if (!errors.isEmpty()) {
            throw new InvalidPricingException("Pricing registration failed", errors);
        }

        pricingRepository.store(targetPricing);

        LOG.info("Registered pricing with id {}", targetPricing.id());
    }

    public Collection<Pricing> allByInstument(InstrumentId instrumentId) {
        LOG.info("Received pricing data request from client for instrumentId = " + instrumentId);

        return pricingRepository.allByInstrument(instrumentId);
    }

    public Collection<Pricing> allByVendor(VendorId vendorId) {
        LOG.info("Received pricing data request from client for vendorId = " + vendorId);

        return pricingRepository.allByVendor(vendorId);
    }

}
