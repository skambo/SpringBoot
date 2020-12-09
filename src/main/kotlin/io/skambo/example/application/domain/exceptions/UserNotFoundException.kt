package io.skambo.example.application.domain.exceptions

class UserNotFoundException(override val message:String): Exception(message) {
}