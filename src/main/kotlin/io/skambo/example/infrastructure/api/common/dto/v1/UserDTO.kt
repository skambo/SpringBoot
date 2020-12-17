package io.skambo.example.infrastructure.api.common.dto.v1

import java.time.OffsetDateTime

data class UserDTO(
        val id: Long,
        val name: String,
        val dateOfBirth: OffsetDateTime,
        val city: String,
        val email: String,
        val phoneNumber: String
) {
}