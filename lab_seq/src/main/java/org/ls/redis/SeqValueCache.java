package org.ls.redis;

import java.math.BigInteger;

public class SeqValueCache { // defines the key and value types to be stored in the cache
    public String key;
    public BigInteger value;

    public SeqValueCache(String key, BigInteger value) {
        this.key = key;
        this.value = value;
    }

    public SeqValueCache() {
    }
}
