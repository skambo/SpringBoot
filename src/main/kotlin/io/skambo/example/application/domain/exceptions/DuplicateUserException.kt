package io.skambo.example.application.domain.exceptions

class DuplicateUserException(override val message:String): Exception(message) {
}