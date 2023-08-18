package org.ls.redis;

import java.math.BigInteger;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SeqValueService { // implements methods to access cache
    private ValueCommands<String, BigInteger> valueCommands;

    public SeqValueService(RedisDataSource ds, ReactiveRedisDataSource reactive) {
        valueCommands = ds.value(BigInteger.class);

        valueCommands.set("0", new BigInteger("0")); // sets know values in the cache
        valueCommands.set("1", new BigInteger("1"));
        valueCommands.set("2", new BigInteger("0"));
        valueCommands.set("3", new BigInteger("1"));
    }

    public BigInteger get(String key) { // return value stored in cache with given key
        BigInteger value = valueCommands.get(key);
        if (value == null) {
            return null;
        }
        return value;
    }

    public boolean exists(String key) { // checks if value is stored in cache
        BigInteger value = valueCommands.get(key);
        if (value == null) {
            return false;
        }
        return true;
    }

    public void set(String key, BigInteger value) { // sets value in the cache
        valueCommands.set(key, value);
    }
}
