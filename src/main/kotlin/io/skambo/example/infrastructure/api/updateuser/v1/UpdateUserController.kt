package io.skambo.example.infrastructure.api.updateuser.v1

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.exceptions.UserNotFoundException
import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
import io.skambo.example.infrastructure.api.updateuser.v1.dto.UpdateUserRequest
import io.skambo.example.infrastructure.api.updateuser.v1.dto.UpdateUserResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController(value = "UpdateUserControllerV1")
@RequestMapping(value = ["v1/"])
class UpdateUserController(private val userService: UserService) {

    @PatchMapping(value = ["updateUser/{id}"])
    @Throws(DuplicateUserException::class, UserNotFoundException::class)
    fun updateUser(
        @RequestBody updateUserRequest: UpdateUserRequest,
        @PathVariable("id") userId:String,
        httpRequest: HttpServletRequest
    ): ResponseEntity<UpdateUserResponse> {
        val user:User = userService.findUserById(userId)
        val updatedUser:User = updateUserFromDto(updateUserRequest, user)

        userService.updateUser(updatedUser)
        val response = UpdateUserResponse(
            header = ApiResponseHelper.createSuccessHeader(httpRequest, updateUserRequest.header),
            id = updatedUser.id!!,
            name = updatedUser.name,
            dateOfBirth = updatedUser.dateOfBirth,
            city = updatedUser.city,
            email = updatedUser.email,
            phoneNumber = updatedUser.phoneNumber
        )
        return ApiResponseHelper.createResponseEntity<UpdateUserResponse>(
            responseHeader = response.header,
            body = response
        )
    }

    private fun updateUserFromDto(updateUserRequest: UpdateUserRequest, user:User): User{
        if(updateUserRequest.name != null) {
            user.name = updateUserRequest.name
        }

        if(updateUserRequest.dateOfBirth != null) {
            user.dateOfBirth = updateUserRequest.dateOfBirth
        }

        if(updateUserRequest.city != null) {
            user.city = updateUserRequest.city
        }

//        if(updateUserRequest.email != null) {
//            user.email = updateUserRequest.email
//        }
//
//        if(updateUserRequest.phoneNumber != null) {
//            user.phoneNumber = updateUserRequest.phoneNumber
//        }
        return user
    }

}