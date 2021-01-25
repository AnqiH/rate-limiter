package com.anqih.newApp

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class NewAppApplicationTests(@Autowired val restTemplate: TestRestTemplate) {
	companion object {
		const val MILLISECONDS = 3600000 // CONSTANT milliseconds in an hour
	}
	private val refillRate = 100
	private var lastRefillTime = System.currentTimeMillis()
	@Test
	fun contextLoads() {
	}


	/*
	* Rate limiter request capacity is 5, hence the first 5
	* request should all return Http code 200
	* */
	@Test
	@Order(1)
	fun `requests not exceeding capacity, return ok response`(){
		var response = restTemplate.getForEntity<String>("/")
		assertEquals(HttpStatus.OK, response.statusCode)
		for(i in 1..4){
			response = restTemplate.getForEntity<String>("/")
		}
		assertEquals(HttpStatus.OK, response.statusCode)
		lastRefillTime = System.currentTimeMillis()
	}

	/*
	* After the request capacity is used up, the new requests
	* should return 429. The rate at which requests can
	* be made is 100 request/hr.
	* Calculate the waiting time for the new token, and compare
	* if it is the same with the time 429 message.
	* */
	@Test
	@Order(2)
	fun `request exceeding capacity, return 429 response and waiting time`(){
		val nextRefill = lastRefillTime + MILLISECONDS/refillRate
		val waitTime = (nextRefill-System.currentTimeMillis())/1000
		val response = restTemplate.getForEntity<String>("/")
		assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.statusCode)
		assertThat(response.body).contains(waitTime.toString())
	}

	/*
	* Wait until a new request si allowed. Request should return 200.
	* */
	@Test
	@Order(3)
	fun `request again when token bucket has been refilled, return 200`(){
		val nextRefill = lastRefillTime + MILLISECONDS/refillRate
		while(System.currentTimeMillis()<nextRefill){}
		val response = restTemplate.getForEntity<String>("/")
		assertEquals(HttpStatus.OK, response.statusCode)
	}

}
