package com.akotkowski.simpledatawarehouse.config

import org.mockito.Mockito
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.services.athena.AthenaClient

@Configuration
@Profile("test")
class AwsConfigTest {
    @Bean
    @Primary
    fun athenaClient(): AthenaClient {
        return Mockito.mock(AthenaClient::class.java)
    }
}