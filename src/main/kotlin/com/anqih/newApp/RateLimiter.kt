package com.anqih.newApp

abstract class RateLimiter() {
    val MILLISECONDS = 3600000 // CONSTANT milliseconds in an hour

    /*
    * allowRequest() returns true if the request can be accepted
    * otherwise returns false
    * */
    abstract fun allowRequest(tokens:Int, userId:String,
                              cache:HashMap<String,Token>):Boolean

    /*
    * waitTime() returns the waiting time (seconds) until the
    * request can be accepted
    * */
    abstract fun waitTime(userId:String, cache:HashMap<String, Token>):Long
}