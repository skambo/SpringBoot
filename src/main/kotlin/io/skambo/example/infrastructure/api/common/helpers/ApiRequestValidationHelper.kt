package io.skambo.example.infrastructure.api.common.helpers

import io.skambo.example.application.domain.exceptions.MalformedRequestException
import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import io.skambo.example.infrastructure.api.common.ErrorCodes
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import java.lang.Exception
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest

object ApiRequestValidationHelper {

    private val responseHelper = ApiResponseHelper

    @Throws(MalformedRequestException::class)
    fun validateTimestampField(timestamp: String) {
        try {
            OffsetDateTime.parse(timestamp)
        } catch (err: Exception) {
            throw MalformedRequestException(
                responseHelper.lookupErrorMessage(ErrorCodes.INVALID_TIMESTAMP_ERR_MSG.value, timestamp))
        }
    }

    @Throws(MalformedRequestException::class)
    fun validateMandatoryRequestHeaders(servletRequest: HttpServletRequest) {
        servletRequest.getHeader(ApiHeaderKey.MESSAGE_ID.value)
            ?: throw MalformedRequestException(
                responseHelper.lookupErrorMessage(ErrorCodes.MISSING_MESSAGE_ID_HEADER_ERR_MSG.value))

        val timestamp = servletRequest.getHeader(ApiHeaderKey.TIMESTAMP.value)
            ?: throw MalformedRequestException(
                responseHelper.lookupErrorMessage(ErrorCodes.MISSING_TIMESTAMP_HEADER_ERR_MSG.value))
        this.validateTimestampField(timestamp)

    }


    /**
     * A simple method to validate expected fields in a request header.
     *
     * @param header {@link Header} expected on all API requests
     * @throws MalformedRequestException an exception indicating that the request is malformed
     */
    @Throws(MalformedRequestException::class)
    fun validateBodyRequestHeader(header: Header, servletRequest: HttpServletRequest) {
        this.validateMandatoryRequestHeaders(servletRequest)
        val headerMessageId = servletRequest.getHeader(ApiHeaderKey.MESSAGE_ID.value)
        val headerGroupId = servletRequest.getHeader(ApiHeaderKey.GROUP_ID.value)

        // both messageId's are not null, yet they differ.
        val bodyMessageId = header.messageId
        if (bodyMessageId != headerMessageId) {
            throw MalformedRequestException(
                responseHelper.lookupErrorMessage(ErrorCodes.CONFLICTING_MESSAGE_ID_ERR_MSG.value))
        }
        // both groupId's are not null, yet they differ.
        val bodyGroupId = header.groupId
        if (headerGroupId != null && bodyGroupId != headerGroupId) {
            throw MalformedRequestException(
                responseHelper.lookupErrorMessage(ErrorCodes.CONFLICTING_GROUP_ID_ERR_MSG.value))
        }
    }
}