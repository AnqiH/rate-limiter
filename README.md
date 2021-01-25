# Rate Limiter
This project implements a Rate Limiter that manages how often a user can make HTTP requests to a specific endpoint.   

The RateLimiter base class can be extended to implement different rate limiting algorithms.
This project implements the [Token Bucket](https://en.wikipedia.org/wiki/Token_bucket#:~:text=The%20token%20bucket%20algorithm%20is,added%20at%20a%20fixed%20rate.&text=They%20may%20be%20enqueued%20for,have%20accumulated%20in%20the%20bucket.)
technique. The token bucket rate limiter is instantiated with 3 parameters: capacity, rate limiting request rate and user store.

The bucket capacity is the number of requests allowed before rate limiting is enforced. Once the capacity has been exceeded, new requests can only be made at 100 requests/hour, otherwise 429 is returned with a message "Rate limite exceeded. Try again in #[n] seconds". An in-memory cache is used to store the request count and the last request timestamp for each user. In the current project the cache is implemented with HashMap, Redis should be used when scaling is required.

### Built with   
Spring Boot 2.4.2  
Gradle 6.7.1

## How to run  

**Clone the repo**  
`git clone https://github.com/AnqiH/rate-limiter.git`  
`cd rate-limiter`  

**Build the project**  
`./gradlew build`  

**Launch the project and specify the port with** `-Dserver.port`  
`java -Dserver.port=8081  -jar build/libs/newApp-0.0.1-SNAPSHOT.jar`   

**Make HTTP requests**  
`curl -i http://localhost:8081`

**Run tests**  
`./gradlew clean test --info`
