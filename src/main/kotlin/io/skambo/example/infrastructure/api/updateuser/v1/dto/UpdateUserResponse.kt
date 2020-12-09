package io.skambo.example.infrastructure.api.updateuser.v1.dto

import java.time.OffsetDateTime

data class UpdateUserResponse(
    val id: Long,
    val name: String,
    val dateOfBirth: OffsetDateTime,
    val city: String,
    val email: String,
    val phoneNumber: String
) {
}