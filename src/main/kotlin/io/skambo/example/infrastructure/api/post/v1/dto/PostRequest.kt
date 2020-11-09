package io.skambo.example.infrastructure.api.post.v1.dto

data class PostRequest(
    val name: String,
    val age: Int,
    val city: String,
    val email: String,
    val phoneNumber: String
)