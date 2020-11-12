package io.skambo.example.infrastructure.api.createuser.v1.dto

import java.time.OffsetDateTime

data class CreateUserResponse (
        val id: Long,
        val name: String,
        val dateOfBirth: OffsetDateTime,
        val city: String,
        val email: String,
        val phoneNumber: String
) {
}