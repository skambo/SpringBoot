package io.skambo.example.infrastructure.api.post.v1

import io.skambo.example.infrastructure.api.post.v1.dto.PostRequest
import io.skambo.example.infrastructure.api.post.v1.dto.PostResponse
import io.skambo.example.infrastructure.persistence.jpa.entities.User
import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

@RestController(value = "PostControllerV1")
@RequestMapping(value = ["v1/"])
class PostController {
    @Autowired
    private val userRepository: UserRepository? = null

    private val counter = AtomicLong()

    @PostMapping(value = ["post"])
    fun post(@RequestBody postRequest: PostRequest): PostResponse {
        val user: User = User (
            id = 1,
            name = postRequest.name,
            age = postRequest.age,
            city = postRequest.city,
            email = postRequest.email,
            phoneNumber = postRequest.phoneNumber
        )
        userRepository?.save(user)
        return PostResponse(counter.incrementAndGet(),
            postRequest.name,
            postRequest.age,
            postRequest.city,
            postRequest.email,
            postRequest.phoneNumber)
    }
}