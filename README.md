# LabSeq-Sequence

## Description

This repository contains a Quarkus application that calculates the LabSeq sequence for a given number. The sequence can be defined as the following:
- n=0 => l(0) = 0
- n=1 => l(1) = 1
- n=2 => l(2) = 0
- n=3 => l(3) = 1
- n>3 => l(n) = l(n-4) + l(n-3)

The application contains the endpoint **{...}/api/labseq/{value}** that can be used to calculate the sequence for the given value.
Example:
- http://localhost:8080/api/labseq/10

It also contains **{...}/docs** which opens an OpenAPI UI page where the previous endpoint can be tested.

## Endpoint and Algorithm
```
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
```
```
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
```

## Caching

This application uses a redis cache which is described below. It uses a quarkus feature called Dev Services 
that allows you to create various datasources without any config. 
What that means practically, is that if you have docker running and have not configured quarkus.redis.hosts, Quarkus will automatically start a Redis container when running tests or dev mode, and automatically configure the connection.

SeqValueCache.java

This file defines the type of values that will be stored.
```
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
```


SeqValueService.java

This file defines the methods used to access the cache. Contains get and set methods and an exists method to check if the required value exists in the cache.
```
  @ApplicationScoped
  public class SeqValueService { // implements methods to access cache
      private ValueCommands<String, BigInteger> valueCommands;
  
      public SeqValueService(RedisDataSource ds, ReactiveRedisDataSource reactive) {
          valueCommands = ds.value(BigInteger.class);
  
          valueCommands.set("0", new BigInteger("0")); // sets known values in the cache
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
```

## How to run 
(REQUIRES DOCKER)

mvn quarkus:dev
