package io.skambo.example.application.domain.model

import java.time.OffsetDateTime

data class User(
    var id: Long? = null,
    val name: String,
    val dateOfBirth: OffsetDateTime,
    val city: String,
    val email: String,
    val phoneNumber: String
) {
}