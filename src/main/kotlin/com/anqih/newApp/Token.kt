package com.anqih.newApp

/*
* Token data class that stores the current tokens in the bucket
* and the timestemp when the bucket is last refilled
* */
data class Token(val curTokens:Long, val lastRefillTime:Long){
}
