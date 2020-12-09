package io.skambo.example.infrastructure.api.fetchusers.v1.dto

import io.skambo.example.infrastructure.api.common.dto.UserDTO

data class FetchUsersResponse(
    val page: Int,
    val totalPages: Int,
    val numberOfUsers: Int,
    val users:List<UserDTO>
) {
}