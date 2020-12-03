package io.skambo.example.infrastructure.api.fetchusers.v1

import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.common.dto.UserDTO
import io.skambo.example.infrastructure.api.fetchusers.v1.dto.FetchUsersResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

@RestController(value = "FetchUsersControllerV1")
@RequestMapping(value = ["v1/"])
class FetchUsersController(private val userService: UserService) {

    @GetMapping(value = ["fetchUsers"])
    fun fetchUsers(
        @RequestParam(value = "orderBy", defaultValue = "name") orderBy: String,
        @RequestParam(value = "sortingDirection", defaultValue = "asc") sortingDirection: String,
        @RequestParam(value = "pageSize", defaultValue = "25") pageSize: String
    ): ResponseEntity<FetchUsersResponse>{
        val users: List<User> = userService.findUsers(pageSize.toInt(), orderBy, sortingDirection)
        val response: FetchUsersResponse = FetchUsersResponse(
            users = users.map {
                user -> UserDTO(
                    id = user.id!!,
                    name = user.name,
                    dateOfBirth = OffsetDateTime.parse(user.dateOfBirth.toString()),
                    city = user.city,
                    email = user.email,
                    phoneNumber = user.phoneNumber
                )}.toList(),
            total = users.size
        )
        return ResponseEntity<FetchUsersResponse>(response, HttpStatus.OK)
    }
}