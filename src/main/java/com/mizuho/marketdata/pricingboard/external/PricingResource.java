package com.mizuho.marketdata.pricingboard.external;

import com.mizuho.marketdata.pricingboard.external.exception.ErrorResult;
import com.mizuho.marketdata.pricingboard.pricing.InstrumentId;
import com.mizuho.marketdata.pricingboard.pricing.PricingEnricher;
import com.mizuho.marketdata.pricingboard.pricing.PricingManagementService;
import com.mizuho.marketdata.pricingboard.pricing.VendorId;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.ok;

@Path("/board")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PricingResource {
    private PricingManagementService pricingManagementService;
    private PricingEnricher pricingEnricher;

    @Autowired
    public PricingResource(PricingManagementService pricingManagementService, PricingEnricher pricingEnricher) {
        this.pricingManagementService = pricingManagementService;
        this.pricingEnricher = pricingEnricher;
    }

    @POST
    @Path("/pricing")
    public Response registerPricing(InboundPricing inboundPricing) {
        if (inboundPricing == null) {
            return Response.status(BAD_REQUEST).entity(new ErrorResult("Missing pricing details")).build();
        }

        pricingManagementService.registerPricing(pricingEnricher.enrich(inboundPricing.toPricing()));

        return ok().build();
    }

    @GET
    @Path("/pricing/instrument/{instrumentId}")
    public Response getPricingForInstrumentId(@PathParam("instrumentId") String instrumentId) {
        return ok(
                pricingManagementService.allByInstument(new InstrumentId(instrumentId)).stream().map(OutboundPricing::fromPricing).collect(toList())
        ).build();
    }

    @GET
    @Path("/pricing/vendor/{vendorId}")
    public Response getPricingForVendorId(@PathParam("vendorId") String vendorId) {
        return ok(
                pricingManagementService.allByVendor(new VendorId(vendorId)).stream().map(OutboundPricing::fromPricing).collect(toList())
        ).build();
    }

}