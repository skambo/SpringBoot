package io.skambo.example.application.domain.exceptions

class MalformedRequestException(override val message:String): Exception(message) {
}