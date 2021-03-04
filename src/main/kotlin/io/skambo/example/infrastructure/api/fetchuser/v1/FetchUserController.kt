package io.skambo.example.infrastructure.api.fetchuser.v1

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.exceptions.UserNotFoundException
import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.UserDTO
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
import io.skambo.example.infrastructure.api.fetchuser.v1.dto.FetchUserResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest

@RestController(value ="FetchUserControllerV1")
@RequestMapping(value = ["v1/"])
class FetchUserController(private val userService: UserService){

    @GetMapping(value = ["fetchUser/{id}"])
    @Throws(UserNotFoundException::class)
    fun fetchUser(
        @PathVariable("id") userId:String,
        httpRequest: HttpServletRequest
    ): ResponseEntity<FetchUserResponse>{
        val user:User = userService.findUserById(userId)
        val header: Header = Header(
            messageId = httpRequest.getHeader(ApiHeaderKey.MESSAGE_ID.value),
            timestamp = OffsetDateTime.parse(httpRequest.getHeader(ApiHeaderKey.TIMESTAMP.value))
        )
        val response = FetchUserResponse(
            header = ApiResponseHelper.createSuccessHeader(httpRequest, header),
            id = user.id!!,
            name = user.name,
            dateOfBirth = OffsetDateTime.parse(user.dateOfBirth.toString()),
            city = user.city,
            email = user.email,
            phoneNumber = user.phoneNumber
        )
        return ApiResponseHelper.createResponseEntity<FetchUserResponse>(
            responseHeader = response.header,
            body = response
        )
    }
}