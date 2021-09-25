package com.akotkowski.simpledatawarehouse.athena

import com.akotkowski.simpledatawarehouse.dto.QueryDTO
import com.amazonaws.services.athena.model.QueryExecutionState
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.athena.AthenaClient
import software.amazon.awssdk.services.athena.model.GetQueryExecutionRequest
import software.amazon.awssdk.services.athena.model.GetQueryResultsRequest
import software.amazon.awssdk.services.athena.model.GetQueryResultsResponse
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse
import javax.annotation.PostConstruct

@Service
class AthenaService(
    val athenaClient: AthenaClient,
    val queryMapper: QueryMapper,
    @Value("\${app.athena.create-view-on-startup:true}") val createViewOnStartup: Boolean
) {
    val logger = LoggerFactory.getLogger(this.javaClass)
    fun postQuery(query: QueryDTO): String {
        val queryString = queryMapper.toQuery(query)
        logger.info("mapped query: {}", queryString)
        return postQuery(queryString).queryExecutionId()
    }

    // TODO: return presigned url instead of direct url
    fun getQueryResultFile(queryId: String): String {
        val queryResult =
            athenaClient.getQueryExecution(GetQueryExecutionRequest.builder().queryExecutionId(queryId).build())
        val queryState = queryResult.queryExecution().status().state().toString()
        if (queryState.equals(QueryExecutionState.SUCCEEDED.toString())) {
            return "https://simple-data-warehouse.s3.eu-central-1.amazonaws.com/output/${queryId}.csv"
        }
        throw AthenaQueryExecutionException("query is not successful, but it may be still running")
    }

    private fun postQuery(query: String): StartQueryExecutionResponse {
        return athenaClient.startQueryExecution {
            it.queryString(query).workGroup("primary").resultConfiguration(
                { it.outputLocation("s3://simple-data-warehouse/output/") })
        }
    }

    @PostConstruct
    fun postConstruct() {
        if (createViewOnStartup) {
            logger.info("creating default view")
            val result = postQuery(
                "CREATE OR REPLACE VIEW campaigns_view AS\n" +
                        "SELECT datasource, campaign, date_parse(daily,'%m/%d/%y') as date, clicks, impressions, cast(clicks as decimal(7,4)) / impressions as ctr FROM \"simple_data_warehouse\".\"campaigns\""
            )
            logger.info("creating default view done with id {}", result.queryExecutionId())
        } else {
            logger.info("SKIP creating default view")
        }
    }

}

class AthenaQueryExecutionException(override val message: String) : Exception()