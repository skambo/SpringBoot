package io.skambo.example.infrastructure.api.greeting.v1

import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.Status
import io.skambo.example.infrastructure.api.greeting.v1.dto.GreetingResponse
import org.springframework.web.bind.annotation.*
import java.time.OffsetDateTime
import java.util.concurrent.atomic.AtomicLong

@RestController(value = "GreetingControllerV1")
@RequestMapping(value = ["v1/"])
class GreetingController {
    private val template = "Hello, %s!"
    private val counter = AtomicLong()

    @GetMapping(value = ["greeting"])
    // @RequestMapping(value = ["/greeting"], method = [RequestMethod.GET])
    fun greeting(@RequestParam(value = "name", defaultValue = "World") name: String?): GreetingResponse {
        val header: Header = Header(messageId = "Header", timestamp = OffsetDateTime.now(), responseStatus = Status(status = "SUCCESS"))
        // throw Exception("It's an error")
        return GreetingResponse(header, counter.incrementAndGet(), String.format(template, name))
    }
}