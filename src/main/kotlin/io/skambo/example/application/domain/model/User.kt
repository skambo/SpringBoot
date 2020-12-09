package io.skambo.example.application.domain.model

import java.time.OffsetDateTime

data class User(
    var id: Long? = null,
    var name: String,
    var dateOfBirth: OffsetDateTime,
    var city: String,
    var email: String,
    var phoneNumber: String
) {
}