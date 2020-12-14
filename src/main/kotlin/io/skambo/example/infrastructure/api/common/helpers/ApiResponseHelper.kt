package io.skambo.example.infrastructure.api.common.helpers

import io.skambo.example.infrastructure.api.common.ApiHeaderKey
import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.Status
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.ResourceBundle
import java.util.UUID
import javax.servlet.http.HttpServletRequest

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.context.request.WebRequest

object ApiResponseHelper {

    private val SUCCESS_STATUS = ResponseStatus.SUCCESS.value
    private val FAILURE_STATUS = ResponseStatus.FAILURE.value
    private val REJECTED_STATUS = ResponseStatus.REJECTED.value
    private val UNKNOWN_STATUS = ResponseStatus.UNKNOWN.value

    private val ERROR_CODES = ResourceBundle.getBundle("error_codes")

    fun createSuccessHeader(httpRequest: HttpServletRequest, header: Header): Header {
        return createBasicHeaderFromHttpRequest(
            httpRequest,
            header,
            Status(SUCCESS_STATUS)
        )
    }

    /**
     * Uses the httpRequest headers and the Header in the original request in
     * order to construct a valid FAILURE Header to be used in the response.
     * This will include an error code and error message.
     *
     * @param httpRequest
     * @param header
     * @param errorCode
     * @param errorMessage
     * @return
     */

    fun createFailureHeader(
        httpRequest: HttpServletRequest,
        header: Header?,
        errorCode: String,
        errorMessage: String
    ): Header {
        return createBasicHeaderFromHttpRequest(
            httpRequest,
            header,
            Status(FAILURE_STATUS, errorCode, errorMessage)
        )
    }

    /**
     * Uses the httpRequest headers and the Header in the original request in
     * order to construct a valid REJECTED Header to be used in the response.
     * This will include an error code and error message.
     *
     * @param httpRequest
     * @param header
     * @param errorCode
     * @param errorMessage
     * @return
     */
    fun createRejectedHeader(
        httpRequest: HttpServletRequest,
        header: Header?,
        errorCode: String,
        errorMessage: String
    ): Header {
        return createBasicHeaderFromHttpRequest(
            httpRequest,
            header,
            Status(REJECTED_STATUS, errorCode, errorMessage)
        )
    }

    /**
     * This method is used when the original request was too invalid to correctly
     * read. In essence the request had no readable Header. On HTTP Header values are available
     *
     * @param webRequest
     * @param errorCode
     * @param errorMessage
     * @return
     */
    fun createRejectedHeader(webRequest: WebRequest, errorCode: String, errorMessage: String): Header {
        return createBasicHeader(
            requestHeaderMessageId = webRequest.getHeader(ApiHeaderKey.MESSAGE_ID.value),
            requestHeaderGroupId = webRequest.getHeader(ApiHeaderKey.GROUP_ID.value),
            status = Status(REJECTED_STATUS, errorCode, errorMessage)
        )
    }

    /**
     * Creates a ResponseEntity that wraps the supplied body. It also uses the
     * header to setup the HTTP headers as well as response code. Given
     * that the API standard state that you MUST give a response, no null checks are
     * performed on the responseHeader as the fields MUST be set.
     *
     * @param responseHeader
     * @param body
     * @return
     */

    fun <T> createResponseEntity(responseHeader: Header, body: T): ResponseEntity<T> {
        val responseEntity: ResponseEntity<T>?

        val headers = org.springframework.http.HttpHeaders()
        headers.add(ApiHeaderKey.GROUP_ID.value, responseHeader.groupId)
        headers.add(ApiHeaderKey.MESSAGE_ID.value, responseHeader.messageId)
        headers.add(ApiHeaderKey.TIMESTAMP.value, responseHeader.timestamp.toString())

        when (responseHeader.responseStatus!!.status) {
            SUCCESS_STATUS ->
                // returns 200
                responseEntity = ResponseEntity(body, headers, HttpStatus.OK)

            REJECTED_STATUS ->
                // returns 400
                responseEntity = ResponseEntity(body, headers, HttpStatus.BAD_REQUEST)

            FAILURE_STATUS ->
                // returns 500
                responseEntity = ResponseEntity(body, headers, HttpStatus.INTERNAL_SERVER_ERROR)

            UNKNOWN_STATUS ->
                // returns 500
                responseEntity = ResponseEntity(body, headers, HttpStatus.INTERNAL_SERVER_ERROR)

            else ->
                // returns 500
                responseEntity = ResponseEntity(body, headers, HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return responseEntity
    }

    /**
     * An internal method used to create a Header with a correctly set
     * messageId, timestamp and groupId. The ResponseStatus is not set. It uses the
     * original HTTPRequest and the Header from the request in order to
     * determine the groupId.
     *
     * @param httpRequest
     * @param requestHeader
     * @return
     */
    fun createBasicHeaderFromHttpRequest(
        httpRequest: HttpServletRequest,
        requestHeader: Header?,
        status: Status
    ): Header {
        val requestHeaderGroupId = httpRequest.getHeader(ApiHeaderKey.GROUP_ID.value)
        val requestHeaderMessageId = httpRequest.getHeader(ApiHeaderKey.MESSAGE_ID.value)
        var requestBodyGroupId: String? = null
        var requestBodyMessageId: String? = null

        if (requestHeader != null) {
            requestBodyGroupId = requestHeader.groupId
            requestBodyMessageId = requestHeader.messageId
        }

        return createBasicHeader(
            requestBodyMessageId,
            requestHeaderMessageId,
            requestBodyGroupId,
            requestHeaderGroupId,
            status
        )
    }

    /**
     * An internal method used to create a Header with a correctly set
     * messageId, timestamp and groupId. responseStatus not set
     *
     * @param httpRequest
     * @return Header
     */
    fun createBasicHeaderFromHttpRequestHeader(httpRequest: HttpServletRequest): Header {
        return Header(
            messageId = httpRequest.getHeader(ApiHeaderKey.MESSAGE_ID.value),
            timestamp = OffsetDateTime.parse(
                httpRequest.getHeader(ApiHeaderKey.TIMESTAMP.value)
            ),
            groupId = httpRequest.getHeader(ApiHeaderKey.GROUP_ID.value)
        )
    }

    /**
     * An internal method used to create a Header with a correctly set
     * messageId, timestamp and groupId. The ResponseStatus is not set.
     *
     * @param requestBodyMessageId
     * @param requestBodyGroupId
     * @param requestHeaderGroupId
     * @return
     */
    fun createBasicHeader(
        requestBodyMessageId: String? = null,
        requestHeaderMessageId: String? = null,
        requestBodyGroupId: String? = null,
        requestHeaderGroupId: String? = null,
        status: Status
    ): Header {
        val responseMessageId = UUID.randomUUID().toString()
        val responseTimestamp = OffsetDateTime.now(ZoneId.of("UTC"))

        // first priority is to use the groupId in the body as the responseGroupId.
        var groupId = requestBodyGroupId

        if (groupId == null) {
            // second priority is to use the groupId in the header as the responseGroupId.
            if (requestHeaderGroupId != null && requestHeaderGroupId.isNotEmpty()) {
                groupId = requestHeaderGroupId
            }

            // third priority is to use the messageId in the body as the responseGroupId.
            if (requestBodyMessageId != null && requestBodyMessageId.isNotEmpty()) {
                groupId = requestBodyMessageId
            }

            // last priority is to use the messageId in the header as the responseGroupId.
            if (requestHeaderMessageId != null && requestHeaderMessageId.isNotEmpty()) {
                groupId = requestHeaderMessageId
            }
        }

        return Header(
            messageId = responseMessageId,
            timestamp = responseTimestamp,
            groupId = groupId,
            responseStatus = status
        )
    }

    /**
     * Utility method to read error codes from the error_codes properties file.
     *
     * @param code
     * @return
     */
    fun lookupErrorCode(code: String): String {
        // err.unknownTimeZone.code
        return ERROR_CODES.getString("err.$code.code")
    }

    /**
     * Utility method to read error message from the error_codes properties file and
     * pass in any params.
     *
     * @param code
     * @param params
     * @return
     */
    fun lookupErrorMessage(code: String, vararg params: String): String {
        // err.invalidRequest.msg
        val errorMessage = ERROR_CODES.getString("err.$code.msg")
        return if (params.isEmpty()) {
            errorMessage
        } else String.format(errorMessage, *params)
    }
}