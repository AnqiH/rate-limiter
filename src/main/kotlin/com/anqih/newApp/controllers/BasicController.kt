package com.anqih.newApp.controllers

import com.anqih.newApp.Token
import com.anqih.newApp.TokenBucketRateLimiter
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest


@Controller
class BasicController {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    /*
    * The cache stores the remaining tokens and the last request time
    * for each user
    * */
    private val cache = HashMap<String, Token>()

    /*
    * Create a rate limiter with a capacity of 5 tokens(requests)
    * After the capacity is used up, new tokens are refilled at rate
    * 100 requests per hour
    * */
    val tbRateLimiter = TokenBucketRateLimiter(5,100,cache)

    @RequestMapping("/")
    fun index(request: HttpServletRequest): ResponseEntity<String>{

        // The client IP address is used as user ID for now
        val ipAddr = request.remoteAddr

        if(tbRateLimiter.allowRequest(1, ipAddr)){
            return ResponseEntity.ok("Hello World\n")
        }

        // If allowRequest() returns false, get the waiting
        // time until the next allowable request and return 429
        val waitTime = tbRateLimiter.waitTime(ipAddr)
        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Rate limit exceeded. Try again in $waitTime seconds\n")
    }

}