package com.anqih.newApp

/*
* RateLimiter Base class
* */
abstract class RateLimiter(private val capacity: Long,
                           private val refillRate: Long, private val cache:HashMap<String, Token>) {

    /*
    * Constant variables used by RateLimiter
    * */
    companion object {
        const val MILLISECONDS = 3600000 // CONSTANT milliseconds in an hour
    }

    /*
    * allowRequest() returns true if the request can be accepted
    * otherwise returns false
    * */
    abstract fun allowRequest(tokens:Int, userId:String):Boolean

    /*
    * waitTime() returns the waiting time (seconds) until the
    * request can be accepted
    * */
    abstract fun waitTime(userId:String):Long
}