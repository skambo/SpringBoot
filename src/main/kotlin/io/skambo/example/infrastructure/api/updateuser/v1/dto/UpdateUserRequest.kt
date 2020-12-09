package io.skambo.example.infrastructure.api.updateuser.v1.dto

import java.time.OffsetDateTime

data class UpdateUserRequest(
    val name: String? = null,
    val dateOfBirth: OffsetDateTime? = null,
    val city: String? = null
//    val email: String? = null ,
//    val phoneNumber: String? = null
) {
}