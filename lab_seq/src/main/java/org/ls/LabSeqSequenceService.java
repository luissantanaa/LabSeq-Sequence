package org.ls;

import java.math.BigInteger;

import org.jboss.logging.Logger;
import org.ls.redis.SeqValueService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LabSeqSequenceService {

    private static final Logger LOG = Logger.getLogger(LabSeqSequence.class);

    @Inject
    SeqValueService service; // injects redis cache service

    public BigInteger calc_request(int value) {
        BigInteger seq_value = new BigInteger("0");

        if (service.exists(String.valueOf(value))) { // checks if requested value is cached
            seq_value = service.get(String.valueOf(value));
        } else {
            for (int i = 4; i <= value - 3; i++) { // calculates unknown values from 4 to value
                if (service.exists(String.valueOf(i))) { // if value exists, skip calculation and continue
                    continue;
                }

                service.set(String.valueOf(i), // calculates value for index i and sets it to cache
                        (service.get(String.valueOf(i - 4)).add(service.get(String.valueOf(i - 3)))));
            }
            seq_value = service.get(String.valueOf(value - 4)).add(service.get(String.valueOf(value - 3))); // calculation
                                                                                                            // of
                                                                                                            // required
                                                                                                            // value
            service.set(String.valueOf(value), seq_value); // sets calculated value to cache
        }

        return seq_value;
    }
}
