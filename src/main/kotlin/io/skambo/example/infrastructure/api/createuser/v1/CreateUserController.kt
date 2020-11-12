package io.skambo.example.infrastructure.api.createuser.v1

import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserRequest
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

@RestController(value = "CreateUserControllerV1")
@RequestMapping(value = ["v1/"])
class CreateUserController(private val userService: UserService){

    @PostMapping(value = ["createUser"])
    fun createUser(@RequestBody createUserRequest: CreateUserRequest): CreateUserResponse {
        val user: User = User (
            name = createUserRequest.name,
            dateOfBirth = createUserRequest.dateOfBirth,
            city = createUserRequest.city,
            email = createUserRequest.email,
            phoneNumber = createUserRequest.phoneNumber
        )
        val createdUser:User = userService.create(user)
        return CreateUserResponse(
                id = createdUser.id!!,
                name = createdUser.name,
                dateOfBirth = createdUser.dateOfBirth,
                email = createdUser.email,
                city = createdUser.city,
                phoneNumber = createdUser.phoneNumber)
    }
}