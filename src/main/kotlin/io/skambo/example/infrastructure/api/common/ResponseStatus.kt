package io.skambo.example.infrastructure.api.common

enum class ResponseStatus(val value: String) {
    SUCCESS("SUCCESS"), // Indicates that the api call was successful.
    FAILURE("FAILURE"), //Indicates that the api call failed internally.
    REJECTED("REJECTED"), // Indicates that the api call failed due to the message failing validation.
    UNKNOWN("UNKNOWN"); // Indicates that the api call is in an unknown state.
}