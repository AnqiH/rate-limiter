## Rate Limiter
This project implements a Rate Limiter that manages how often a user can make HTTP requests to 
a specific endpoint.   

The RateLimiter base class can be extended to implement different rate limiting algorithms.
This project implements the [Token Bucket](https://en.wikipedia.org/wiki/Token_bucket#:~:text=The%20token%20bucket%20algorithm%20is,added%20at%20a%20fixed%20rate.&text=They%20may%20be%20enqueued%20for,have%20accumulated%20in%20the%20bucket.)
technique. An initial capacity is specified as the number request allowed before rate 
limiting is enforced, after the capacity has been reached, new requests can only be made 
at 100 requests/hour. An in-memory cache is used to store the request count and timestamp
for each user.

---
### How to run  


