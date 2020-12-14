package io.skambo.example.infrastructure.api.common

enum class ApiHeaderKey(val value: String) {
    MESSAGE_ID("messageId"),
    GROUP_ID("groupId"),
    TIMESTAMP("timestamp")
}