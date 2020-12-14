package io.skambo.example.infrastructure.api.fetchuser.v1

import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.common.dto.v1.UserDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

@RestController(value ="FetchUserControllerV1")
@RequestMapping(value = ["v1/"])
class FetchUserController(private val userService: UserService){

    @GetMapping(value = ["fetchUser/{id}"])
    fun fetchUser(@PathVariable("id") userId:Long): ResponseEntity<UserDTO>{
        val user:User = userService.findUserById(userId)
        val userDTO:UserDTO = UserDTO(
            id = user.id!!,
            name = user.name,
            dateOfBirth = OffsetDateTime.parse(user.dateOfBirth.toString()),
            city = user.city,
            email = user.email,
            phoneNumber = user.phoneNumber
        )
        return ResponseEntity(userDTO, HttpStatus.OK)
    }
}