package com.akotkowski.simpledatawarehouse.dto

data class QueryDTO(
    val select: List<String>,
    val where: String? = null,
    val orderBy: String? = null,
    val groupBy: String? = null,
    val having: String? = null,
    val limit: Int
) {

}
