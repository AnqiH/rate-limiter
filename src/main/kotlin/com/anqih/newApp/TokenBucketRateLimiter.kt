package com.anqih.newApp

import org.slf4j.LoggerFactory


/*
* TokenBucketRateLimiter is implemented with Token Bucket algorithm. Http requests
* are only accepted if there are sufficient number of tokens in the bucket. After the
* initial request capacity is used up, new tokens are refilled at a specific rate so
* that the frequency of Http requests is reduced.
* */
class TokenBucketRateLimiter(private val capacity: Long,
                             private val refillRate: Long, private val cache:HashMap<String, Token>)
    : RateLimiter(capacity, refillRate,cache) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /*
    * Return true if the request can be accepted
    * */
    @Synchronized override fun allowRequest(tokens:Int, userId:String):Boolean {
        // If the user is not recorded in the cache,
        // initialize a new token bucket
        if(!cache.containsKey(userId)){
            val lastStatus = Token(capacity, System.currentTimeMillis())
            cache[userId] = lastStatus
        }
        // refill the bucket
        refill(userId)
        // Check if there are available tokens for the request
        var curTokens = cache[userId]!!.curTokens
        if(curTokens - tokens >= 0){
            curTokens -= tokens
            cache[userId]=Token(curTokens, System.currentTimeMillis())
            return true
        }
        return false
    }

    override fun waitTime(userId:String):Long {
        // Update the wait time until the next token
        // bucket refill
        val lastRefillTime = cache[userId]!!.lastRefillTime
        val nextRefill = lastRefillTime + MILLISECONDS/refillRate
        return (nextRefill-System.currentTimeMillis())/1000
    }

    /*
    * Refill the token bucket based on refill rate and the
    * elapsed time since last refill
    * */
    private fun refill(userId:String){
        val curTime = System.currentTimeMillis()
        // The current token count in the bucket
        var curTokens = cache[userId]!!.curTokens
        var lastRefillTime = cache[userId]!!.lastRefillTime

        // Calculate and update the token count after refill
        val addTokens = (curTime-lastRefillTime)*refillRate/MILLISECONDS

        // Return if there isn't new tokens to refill
        if(addTokens<1) return

        // Update the number of current tokens
        curTokens = Math.min(capacity, curTokens+addTokens)
        lastRefillTime = curTime
        cache[userId] = Token(curTokens, lastRefillTime)
    }
}