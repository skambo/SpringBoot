package io.skambo.example.application.domain.exceptions

class UserNotFoundException(override val message:String, override val cause: Throwable? = null): Exception(message, cause) {
}