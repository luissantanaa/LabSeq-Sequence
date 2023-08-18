package org.ls;

import java.math.BigInteger;

import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.ls.redis.SeqValueService;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("/api")
public class LabSeqSequence {

    private static final Logger LOG = Logger.getLogger(LabSeqSequence.class);

    @Inject
    LabSeqSequenceService service;

    @POST // Type of the request
    @Path("/labseq/{value}") // Url for the endpoint
    @APIResponse(responseCode = "200") // Successful request response
    @APIResponse(responseCode = "400", description = "Value must be equal or greater than 0") // Bad request response
    public Response calc_request(@PathParam("value") int value) {

        // Checks if received value is valid, returns 400-Bad Request if not
        if (value < 0) {
            return Response.status(400).entity("Value must be equal or greater than 0").build();
        }

        BigInteger seq_value = service.calc_request(value); // calls sequence calculator method

        return Response.ok(seq_value).build();
    }
}
