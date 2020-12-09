package io.skambo.example.infrastructure.api.deleteuser.v1

import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.deleteuser.v1.dto.DeleteUserResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController(value = "DeleteUserController")
@RequestMapping(value = ["v1/"])
class DeleteUserController(private val userService: UserService) {

    @RequestMapping(value = ["deleteUser/{id}"])
    fun deleteUser(@PathVariable ("id") userId:Long): ResponseEntity<DeleteUserResponse>{
        userService.deleteUser(userId)
        val response:DeleteUserResponse = DeleteUserResponse(status = "SUCCESS")
        return ResponseEntity(response, HttpStatus.NO_CONTENT)
    }
}