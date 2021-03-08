package io.skambo.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["io.skambo.example"])
class SpringExampleApplication

fun main(args: Array<String>) {
    runApplication<SpringExampleApplication>(*args)
}
