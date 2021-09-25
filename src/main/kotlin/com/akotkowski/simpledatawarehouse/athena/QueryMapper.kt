package com.akotkowski.simpledatawarehouse.athena

import com.akotkowski.simpledatawarehouse.dto.QueryDTO
import org.springframework.stereotype.Service

@Service
class QueryMapper() {
    fun toQuery(query: QueryDTO): String {
        validateQuery(query)
        var selectRows = query.select.joinToString()
        val tableName = "\"simple_data_warehouse\".\"campaigns_view\""
        // TODO: use string builder for better performance
        var sql = "select $selectRows from $tableName"
        query.where?.let { sql += " where $it" }
        query.groupBy?.let { sql += " group by $it" }
        query.having?.let { sql += " having $it" }
        query.orderBy?.let { sql += " order by $it" }
        sql += " limit " + query.limit
        return sql
    }

    // FIXME: queries should be more restricted to avoid abuse usage
    private fun validateQuery(query: QueryDTO) {
        query.select.forEach {
            checkNoSqlInjection(it)
        }
        query.orderBy?.let { checkNoSqlInjection(it) }
        query.where?.let { checkNoSqlInjection(it) }
        query.groupBy?.let { checkNoSqlInjection(it) }
        query.limit.takeIf { i -> i > 100 }?.run { throw QueryValidationException("Query limit to high, max is 100") }
    }

    private fun checkNoSqlInjection(query: String) {
        if (query.contains("--")
            || query.contains("/*")
            || !query.matches("[\\s\\(\\)\\w\\|\\&\\/\\-\\*\\+\\'\\!><=]*".toRegex())
        ) {
            throw QueryValidationException("Invalid query")
        }
    }
}

class QueryValidationException(override val message: String) : Exception()