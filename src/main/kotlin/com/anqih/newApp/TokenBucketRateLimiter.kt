package com.anqih.newApp

import org.slf4j.LoggerFactory

class TokenBucketRateLimiter(private val capacity: Long,
                             private val refillRate: Long) : RateLimiter() {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Synchronized override fun allowRequest(tokens:Int, userId:String,
                                            cache:HashMap<String,Token>):Boolean {
        // If the user is not stored in the cache,
        // initialize a new token bucket
        if(!cache.containsKey(userId)){
            var lastStatus = Token(capacity, System.currentTimeMillis())
            cache[userId] = lastStatus
        }
        // refill the bucket
        refill(userId, cache)
        var curTokens = cache[userId]!!.curTokens
        if(curTokens - tokens >= 0){
            curTokens -= tokens
            cache[userId]=Token(curTokens, System.currentTimeMillis())
            return true
        }
        return false
    }

    override fun waitTime(userId:String, cache:HashMap<String, Token>):Long {
        // Update the wait time until the next token
        // bucket refill
        val lastRefillTime = cache[userId]!!.lastRefillTime
        val nextRefill = lastRefillTime + MILLISECONDS/refillRate
        val waitTime= (nextRefill-System.currentTimeMillis())/1000
        logger.info("Wait time $waitTime")
        return waitTime
    }

    private fun refill(userId:String, cache:HashMap<String, Token>){
        val curTime = System.currentTimeMillis()
        // The current token count in the bucket
        var curTokens = cache[userId]!!.curTokens
        var lastRefillTime = cache[userId]!!.lastRefillTime

        // Calculate and update the token count after refill
        val addTokens = (curTime-lastRefillTime)*refillRate/MILLISECONDS
        logger.info("new tokens $addTokens")

        // Return if there isn't new tokens to refill
        if(addTokens<1) return

        curTokens = Math.min(capacity, curTokens+addTokens)
        lastRefillTime = curTime
        cache[userId] = Token(curTokens, lastRefillTime)
    }
}