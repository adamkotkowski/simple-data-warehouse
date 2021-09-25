package com.akotkowski.simpledatawarehouse.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.factory.PasswordEncoderFactories


// Simple security just for POC purposes, as the API is exposed to the World
@Configuration
@EnableWebSecurity
class AppWebSecurityConfigurer : WebSecurityConfigurerAdapter() {

    override fun configure(auth: AuthenticationManagerBuilder) {
        val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
        auth
            .inMemoryAuthentication()
            .withUser("user")
            .password(encoder.encode("cf84fef9-d49d-4dd8-8f63-ebddd2f534a7"))
            .roles("USER")
    }

    override fun configure(http: HttpSecurity) {
        http
            .csrf().disable().authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic()
    }
}