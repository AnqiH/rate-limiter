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
    * The cache stores the remaining teokens and the last request time
    * for each user
    * */
    private var cache = HashMap<String, Token>()

    /*
    * Create a rate limiter instance with a capacity of 5 tokens(requests)
    * After the requests are used up, new tokens are refilled at rate
    * 100 requests per hour
    * */
    val tbRateLimiter = TokenBucketRateLimiter(5,100)

    @RequestMapping("/")
    fun index(request: HttpServletRequest): ResponseEntity<String>{

        // The client IP address is used as user ID
        val ipAddr = request.remoteAddr

        if(tbRateLimiter.allowRequest(1, ipAddr, cache)){
            return ResponseEntity.ok("Hello World\n")
        }
        val waitTime = tbRateLimiter.waitTime(ipAddr, cache)
        return ResponseEntity
            .status(HttpStatus.TOO_MANY_REQUESTS)
            .body("Rate limit exceeded. Try again in $waitTime seconds\n")
    }

}