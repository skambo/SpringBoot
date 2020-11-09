package io.skambo.example.infrastructure.api.greeting.v1

import io.skambo.example.infrastructure.api.greeting.v1.dto.GreetingResponse
import org.springframework.web.bind.annotation.*
import java.util.concurrent.atomic.AtomicLong

@RestController(value = "GreetingControllerV1")
@RequestMapping(value = ["v1/"])
class GreetingController {
    private val template = "Hello, %s!"
    private val counter = AtomicLong()

    @GetMapping(value = ["greeting"])
    // @RequestMapping(value = ["/greeting"], method = [RequestMethod.GET])
    fun greeting(@RequestParam(value = "name", defaultValue = "World") name: String?): GreetingResponse {
        return GreetingResponse(counter.incrementAndGet(), String.format(template, name))
    }
}