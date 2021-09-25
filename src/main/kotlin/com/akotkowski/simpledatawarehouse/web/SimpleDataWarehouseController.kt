package com.akotkowski.simpledatawarehouse.web

import com.akotkowski.simpledatawarehouse.athena.AthenaService
import com.akotkowski.simpledatawarehouse.dto.QueryCreatedDTO
import com.akotkowski.simpledatawarehouse.dto.QueryDTO
import com.akotkowski.simpledatawarehouse.dto.QueryResultDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import io.github.bucket4j.Bucket4j

import io.github.bucket4j.Refill

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.local.LocalBucket
import java.time.Duration


@RestController
class SimpleDataWarehouseController(val athenaService: AthenaService) {
    val logger = LoggerFactory.getLogger(this.javaClass)
    val bucket: LocalBucket

    init {
        // limit post usage to 6 requests per minute
        val limit = Bandwidth.classic(6, Refill.greedy(6, Duration.ofMinutes(1)))
        this.bucket = Bucket4j.builder()
            .addLimit(limit)
            .build()
    }


    @PostMapping(
        path = ["/query"], consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun postQuery(@RequestBody queryDTO: QueryDTO): ResponseEntity<QueryCreatedDTO> {
        logger.info("query: {}", queryDTO)
        if (bucket.tryConsume(1)) {
            val queryId = athenaService.postQuery(queryDTO)
            logger.info("query created: {}", queryId)
            return ResponseEntity(QueryCreatedDTO(id = queryId), HttpStatus.CREATED)
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
    }

    // TODO: handle not found response
    @GetMapping("/query/{id}")
    fun getQueryResults(@PathVariable("id") queryId: String): QueryResultDTO {
        val queryResultUrl = athenaService.getQueryResultFile(queryId)
        logger.info("query result file: {}", queryResultUrl)
        return QueryResultDTO(resultFile = queryResultUrl)
    }
}