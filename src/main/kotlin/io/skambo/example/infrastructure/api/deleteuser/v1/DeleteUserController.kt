package io.skambo.example.infrastructure.api.deleteuser.v1

import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import io.skambo.example.infrastructure.api.createuser.v1.CreateUserController
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
import io.skambo.example.infrastructure.api.deleteuser.v1.dto.DeleteUserResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController(value = "DeleteUserController")
@RequestMapping(value = ["v1/"])
class DeleteUserController(private val userService: UserService) {

    // TODO Fix adherence to API standards
    @RequestMapping(value = ["deleteUser/{id}"])
    fun deleteUser(
        @PathVariable ("id") userId:String,
        request: HttpServletRequest
    ): ResponseEntity<DeleteUserResponse>{
        userService.deleteUser(userId)
        val header: Header = ApiResponseHelper.createBasicHeaderFromHttpRequestHeader(httpRequest = request)
        val response:DeleteUserResponse = DeleteUserResponse(
            header = ApiResponseHelper.createSuccessHeader(request, header)
        )
        return ApiResponseHelper.createResponseEntity<DeleteUserResponse>(
            responseHeader = response.header,
            body = response,
            httpStatusCode = HttpStatus.OK
        )
    }
}