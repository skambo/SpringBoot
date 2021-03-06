package io.skambo.example.infrastructure.api.fetchusers.v1

import io.skambo.example.application.domain.model.User
import io.skambo.example.application.helpers.SortingAndPaginationHelper
import io.skambo.example.application.services.UserService
import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.UserDTO
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import io.skambo.example.infrastructure.api.fetchuser.v1.dto.FetchUserResponse
import io.skambo.example.infrastructure.api.fetchusers.v1.dto.FetchUsersResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest


@RestController(value = "FetchUsersControllerV1")
@RequestMapping(value = ["v1/"])
class FetchUsersController(private val userService: UserService) {

    @GetMapping(value = ["fetchUsers"])
    fun fetchUsers(
        @RequestParam(value = "orderBy", defaultValue = "name") orderBy: String,
        @RequestParam(value = "sortingDirection", defaultValue = "asc") sortingDirection: String,
        @RequestParam(value = "pageSize", defaultValue = "25") pageSize: String,
        @RequestParam(value = "pageNumber", defaultValue = "1") pageNumber: String,
        @RequestParam(value = "filters", defaultValue = "") filters: String,
        httpRequest: HttpServletRequest
    ): ResponseEntity<FetchUsersResponse>{

        // Checks that the page number starts at 0
        val page:Int = if (pageNumber.toInt()-1 < 0) 0 else pageNumber.toInt()-1
        val pageRequest:PageRequest = SortingAndPaginationHelper.createPageRequest(
            page, pageSize.toInt(), sortingDirection, orderBy.split(",")
        )
        val usersPage: Page<User> = userService.findUsers(pageRequest, filters)

        val header: Header = Header(
            messageId = httpRequest.getHeader(ApiHeaderKey.MESSAGE_ID.value),
            timestamp = OffsetDateTime.parse(httpRequest.getHeader(ApiHeaderKey.TIMESTAMP.value))
        )

        val response: FetchUsersResponse = FetchUsersResponse(
            header = ApiResponseHelper.createSuccessHeader(httpRequest, header),
            page = page + 1,
            totalPages = usersPage.totalPages,
            numberOfUsers = usersPage.numberOfElements,
            users = usersPage.map {
                user -> UserDTO(
                    id = user.id!!,
                    name = user.name,
                    dateOfBirth = OffsetDateTime.parse(user.dateOfBirth.toString()),
                    city = user.city,
                    email = user.email,
                    phoneNumber = user.phoneNumber
                )}.toList().toTypedArray()
        )
        return ApiResponseHelper.createResponseEntity<FetchUsersResponse>(
            responseHeader = response.header,
            body = response
        )
    }
}