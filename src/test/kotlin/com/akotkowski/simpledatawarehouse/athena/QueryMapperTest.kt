package com.akotkowski.simpledatawarehouse.athena

import com.akotkowski.simpledatawarehouse.dto.QueryDTO
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class QueryMapperTest {

    lateinit var sut : QueryMapper

    @BeforeEach
    fun init(){
        sut = QueryMapper()
    }

    @Test
    fun `should map to query`() {
        // given
        val queryDTO = QueryDTO(
            select = Arrays.asList("*", "count(clicks)"),
            limit = 10,
            where = "something = 'this' and something_else > 5",
            orderBy = "someything_else",
            groupBy = "clicks",
            having = "type = 'mouse'"
        )
        val expectedQuery = "select *, count(clicks) " +
                "from \"simple_data_warehouse\".\"campaigns_view\" " +
                "where something = 'this' and something_else > 5 " +
                "group by clicks having type = 'mouse' " +
                "order by someything_else limit 10"

        // when
        val result = sut.toQuery(queryDTO)

        // then
        Assertions.assertThat(result).isEqualTo(expectedQuery)
    }

    @Test
    fun `should avoid sql injection with not allowed character`() {
        // given
        val queryDTO = QueryDTO(
            select = Arrays.asList("*", "count(clicks)"),
            limit = 10,
            where = "; DROP TABLE"
        )

        // when
        val th = Assertions.catchThrowable { sut.toQuery(queryDTO) }

        // then
        Assertions.assertThat(th).isNotNull()
    }

    @Test
    fun `should avoid sql injection with comment`() {
        // given
        val queryDTO = QueryDTO(
            select = Arrays.asList("*", "count(clicks)"),
            limit = 10,
            where = "-- OR 1=1"
        )

        // when
        val th = Assertions.catchThrowable { sut.toQuery(queryDTO) }

        // then
        Assertions.assertThat(th).isNotNull()
    }
}