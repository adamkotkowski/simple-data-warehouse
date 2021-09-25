package com.akotkowski.simpledatawarehouse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SimpleDataWarehouseApplication

fun main(args: Array<String>) {
    runApplication<SimpleDataWarehouseApplication>(*args)
}
