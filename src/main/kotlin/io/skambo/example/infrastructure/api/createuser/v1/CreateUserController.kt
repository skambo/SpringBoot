package io.skambo.example.infrastructure.api.createuser.v1

import io.skambo.example.MigrationApplication
import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserRequest
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
import io.skambo.example.infrastructure.api.fetchuser.v1.dto.FetchUserResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController(value = "CreateUserControllerV1")
@RequestMapping(value = ["v1/"])
class CreateUserController(private val userService: UserService){
    private val LOGGER = LoggerFactory.getLogger(CreateUserController::class.java)

    @PostMapping(value = ["createUser"])
    fun createUser(
        @RequestBody createUserRequest: CreateUserRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<CreateUserResponse> {
        LOGGER.info("Request received: $createUserRequest")
        val user: User = User (
            name = createUserRequest.name,
            dateOfBirth = createUserRequest.dateOfBirth,
            city = createUserRequest.city,
            email = createUserRequest.email,
            phoneNumber = createUserRequest.phoneNumber
        )
        val createdUser:User = userService.createUser(user)
        val response = CreateUserResponse(
            header = ApiResponseHelper.createSuccessHeader(httpRequest, createUserRequest.header),
            id = createdUser.id!!,
            name = createdUser.name,
            dateOfBirth = createdUser.dateOfBirth,
            email = createdUser.email,
            city = createdUser.city,
            phoneNumber = createdUser.phoneNumber)
        val responseEntity: ResponseEntity<CreateUserResponse> = ApiResponseHelper.createResponseEntity<CreateUserResponse>(
            responseHeader = response.header,
            body = response,
            httpStatusCode = HttpStatus.CREATED
        )
        LOGGER.info("Response returned: $responseEntity")
        return responseEntity
    }
}