package io.skambo.example.infrastructure.api.greeting.v2

import io.skambo.example.infrastructure.api.greeting.v2.dto.GreetingResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

@RestController(value = "GreetingControllerV2")
@RequestMapping(value = ["v2/"])
class GreetingController {
    private val templateV2 = "Hello, %s from %s"
    private val counter = AtomicLong()

    @GetMapping(value = ["greeting"])
    fun greetingV2(@RequestParam(value = "name", defaultValue = "World") name:String?,
                   @RequestParam(value = "city", defaultValue = "") city:String?): GreetingResponse{
        return GreetingResponse(counter.incrementAndGet(), String.format(templateV2, name, city), "SUCCESS")
    }
}