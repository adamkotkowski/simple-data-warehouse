package com.akotkowski.simpledatawarehouse.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.services.athena.AthenaClient


@Configuration
@Profile("!test")
class AwsConfig {

    @Bean
    fun athenaClient(): AthenaClient {
        return AthenaClient.builder().build()
    }
}