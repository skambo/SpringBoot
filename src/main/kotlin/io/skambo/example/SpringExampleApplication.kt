package io.skambo.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication(scanBasePackages = ["io.skambo.example"])
open class SpringExampleApplication

fun main(args: Array<String>) {
    runApplication<SpringExampleApplication>(*args)
}
