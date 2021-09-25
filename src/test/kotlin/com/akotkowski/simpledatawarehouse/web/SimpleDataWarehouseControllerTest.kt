package com.akotkowski.simpledatawarehouse.web

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpRequestInterceptor


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = [SecurityAutoConfiguration::class])
internal class SimpleDataWarehouseControllerTest {

    private lateinit var url: String

    @LocalServerPort
    protected var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    @Disabled("Not implemented")
    fun `should return correct response`() {
        // given
        val url = "http://localhost:$port/query/{queryId}"

        // when
        val response = restTemplate.getForEntity(url, String::class.java, "testQuery")

        Assertions.assertThat(
            response.statusCode
        ).isEqualTo(HttpStatus.OK)
    }
}