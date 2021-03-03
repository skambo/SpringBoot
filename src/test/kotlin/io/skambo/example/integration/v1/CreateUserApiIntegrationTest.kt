package io.skambo.example.integration.v1

import io.skambo.example.ApiTestHelper
import io.skambo.example.TestHelper
import io.skambo.example.infrastructure.api.common.ErrorCodes
import io.skambo.example.infrastructure.api.common.ResponseStatus
import io.skambo.example.infrastructure.api.common.dto.v1.Header
import io.skambo.example.infrastructure.api.common.dto.v1.Status
import io.skambo.example.infrastructure.api.common.helpers.ApiResponseHelper
import io.skambo.example.infrastructure.api.createuser.v1.dto.CreateUserResponse
import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import io.skambo.example.integration.BaseApiIntegrationTest
import io.skambo.example.integration.utils.TestScenario
import org.junit.Assert
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class CreateUserApiIntegrationTest: BaseApiIntegrationTest<String, CreateUserResponse>() {

    private final val header: Header = ApiTestHelper.createTestHeader()
    private final val name: String = "Anne"
    private final val dateOfBirth: OffsetDateTime = LocalDateTime
        .parse("2017-02-03 12:30:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        .atOffset(ZoneOffset.UTC)
    private final val city: String = "Nairobi"
    private final val email: String = "anne@gmail.com"
    private final val phoneNumber: String = "1224"

    override val endpoint: String = "/api/v1/createUser"

    override val httpMethod: HttpMethod = HttpMethod.POST

    override val requestBody: String = TestHelper.convertToJsonString(
        mapOf(
            "header" to header,
            "name" to name,
            "dateOfBirth" to dateOfBirth,
            "city" to city,
            "email" to email,
            "phoneNumber" to phoneNumber
        )
    )

    override fun createTestScenarios(): List<TestScenario<String, CreateUserResponse>> {
         return listOf(
             successScenario(),
             userExistsScenario(),
             missingNameFieldScenario(),
             missingDateOfBirthFieldScenario(),
             missingCityScenario(),
             missingEmailScenario(),
             missingPhoneNumberScenario(),
             missingHeaderScenario(),
             missingRequestBodyScenario()
         )
    }

    private fun successScenario(): TestScenario<String, CreateUserResponse>{
        //This is a high order function
        val preScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val postScenario: () -> Unit = {
            val optionalUser: Optional<UserDataModel> = this.userRepository.findByEmail(email)
            Assert.assertTrue(optionalUser.isPresent)

            val persistedUser: UserDataModel = optionalUser.get()
            Assert.assertEquals(name, persistedUser.name)
            Assert.assertEquals(dateOfBirth.toString(), persistedUser.dateOfBirth)
            Assert.assertEquals(city, persistedUser.city)
            Assert.assertEquals(email, persistedUser.email)
            Assert.assertEquals(phoneNumber, persistedUser.phoneNumber)

            //We're cleaning up the created user
            this.userRepository.delete(persistedUser)
        }

        return TestScenario(
            description = "Create user success scenario",
            endpoint = this.endpoint,
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.CREATED,
            expectedResponseBody = CreateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.SUCCESS.value
                    )
                ),
                id = 1L,
                name = name,
                dateOfBirth = dateOfBirth,
                city = city,
                email = email,
                phoneNumber = phoneNumber
            ),
            responseClass = CreateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun userExistsScenario(): TestScenario<String, CreateUserResponse>{
        //This is a high order function
        val preScenario: () -> Unit = {
            val existingUser: UserDataModel = UserDataModel(
                name = name,
                dateOfBirth = dateOfBirth.toString(),
                city = city,
                email = email,
                phoneNumber = phoneNumber
            )

            this.userRepository.save(existingUser)

            Assert.assertTrue(this.userRepository.findByEmail(email).isPresent)
            Assert.assertTrue(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val postScenario: () -> Unit = {
            val optionalUser: Optional<UserDataModel> = this.userRepository.findByEmail(email)
            Assert.assertTrue(optionalUser.isPresent)

            //We're cleaning up the created user
            this.userRepository.delete(optionalUser.get())
        }

        return TestScenario(
            description = "Existing user scenario",
            endpoint = this.endpoint,
            httpHeaders = this.httpHeaders,
            requestBody = this.requestBody,
            expectedHttpStatus = HttpStatus.BAD_REQUEST,
            expectedResponseBody = CreateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.REJECTED.value,
                        errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.DUPLICATE_USER_ERR.value),
                        errorMessage = ApiResponseHelper.lookupErrorMessage(ErrorCodes.DUPLICATE_USER_ERR.value)
                    )
                )
            ),
            responseClass = CreateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun missingNameFieldScenario(): TestScenario<String, CreateUserResponse> {
        val preScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val postScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val missingNameRequestBody: String = TestHelper.convertToJsonString(
            mapOf(
                "header" to header,
                "dateOfBirth" to dateOfBirth,
                "city" to city,
                "email" to email,
                "phoneNumber" to phoneNumber
            )
        )

        return TestScenario(
            description = "Missing names scenario",
            endpoint = this.endpoint,
            httpHeaders = this.httpHeaders,
            requestBody = missingNameRequestBody,
            expectedHttpStatus = HttpStatus.BAD_REQUEST,
            expectedResponseBody = CreateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.REJECTED.value,
                        errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                        errorMessage = ApiResponseHelper.lookupErrorMessage(
                            ErrorCodes.MISSING_PARAMETER_ERR_MSG.value, "name"
                        )
                    )
                )
            ),
            responseClass = CreateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun missingDateOfBirthFieldScenario(): TestScenario<String, CreateUserResponse> {
        val preScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val postScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val missingDateOfBirthRequestBody: String = TestHelper.convertToJsonString(
            mapOf(
                "header" to header,
                "name" to name,
                "city" to city,
                "email" to email,
                "phoneNumber" to phoneNumber
            )
        )

        return TestScenario(
            description = "Missing date of birth scenario",
            endpoint = this.endpoint,
            httpHeaders = this.httpHeaders,
            requestBody = missingDateOfBirthRequestBody,
            expectedHttpStatus = HttpStatus.BAD_REQUEST,
            expectedResponseBody = CreateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.REJECTED.value,
                        errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                        errorMessage = ApiResponseHelper.lookupErrorMessage(
                            ErrorCodes.MISSING_PARAMETER_ERR_MSG.value, "dateOfBirth"
                        )
                    )
                )
            ),
            responseClass = CreateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun missingCityScenario(): TestScenario<String, CreateUserResponse> {
        val preScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val postScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val missingCityRequestBody: String = TestHelper.convertToJsonString(
            mapOf(
                "header" to header,
                "name" to name,
                "dateOfBirth" to dateOfBirth,
                "email" to email,
                "phoneNumber" to phoneNumber
            )
        )

        return TestScenario(
            description = "Missing city scenario",
            endpoint = this.endpoint,
            httpHeaders = this.httpHeaders,
            requestBody = missingCityRequestBody,
            expectedHttpStatus = HttpStatus.BAD_REQUEST,
            expectedResponseBody = CreateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.REJECTED.value,
                        errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                        errorMessage = ApiResponseHelper.lookupErrorMessage(
                            ErrorCodes.MISSING_PARAMETER_ERR_MSG.value, "city"
                        )
                    )
                )
            ),
            responseClass = CreateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun missingEmailScenario(): TestScenario<String, CreateUserResponse> {
        val preScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val postScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val missingCityRequestBody: String = TestHelper.convertToJsonString(
            mapOf(
                "header" to header,
                "name" to name,
                "dateOfBirth" to dateOfBirth,
                "city" to city,
                "phoneNumber" to phoneNumber
            )
        )

        return TestScenario(
            description = "Missing email scenario",
            endpoint = this.endpoint,
            httpHeaders = this.httpHeaders,
            requestBody = missingCityRequestBody,
            expectedHttpStatus = HttpStatus.BAD_REQUEST,
            expectedResponseBody = CreateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.REJECTED.value,
                        errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                        errorMessage = ApiResponseHelper.lookupErrorMessage(
                            ErrorCodes.MISSING_PARAMETER_ERR_MSG.value, "email"
                        )
                    )
                )
            ),
            responseClass = CreateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun missingPhoneNumberScenario(): TestScenario<String, CreateUserResponse> {
        val preScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val postScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val missingCityRequestBody: String = TestHelper.convertToJsonString(
            mapOf(
                "header" to header,
                "name" to name,
                "dateOfBirth" to dateOfBirth,
                "city" to city,
                "email" to email
            )
        )

        return TestScenario(
            description = "Missing phone number scenario",
            endpoint = this.endpoint,
            httpHeaders = this.httpHeaders,
            requestBody = missingCityRequestBody,
            expectedHttpStatus = HttpStatus.BAD_REQUEST,
            expectedResponseBody = CreateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.REJECTED.value,
                        errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                        errorMessage = ApiResponseHelper.lookupErrorMessage(
                            ErrorCodes.MISSING_PARAMETER_ERR_MSG.value, "phoneNumber"
                        )
                    )
                )
            ),
            responseClass = CreateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }

    private fun missingHeaderScenario(): TestScenario<String, CreateUserResponse> {
        val preScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val postScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val missingHeaderBody: String = TestHelper.convertToJsonString(
            mapOf(
                "name" to name,
                "dateOfBirth" to dateOfBirth,
                "city" to city,
                "email" to email,
                "phoneNumber" to phoneNumber
            )
        )

        return TestScenario(
            description = "Missing header scenario",
            endpoint = this.endpoint,
            httpHeaders = this.httpHeaders,
            requestBody = missingHeaderBody,
            expectedHttpStatus = HttpStatus.BAD_REQUEST,
            expectedResponseBody = CreateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.REJECTED.value,
                        errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                        errorMessage = ApiResponseHelper.lookupErrorMessage(
                            ErrorCodes.MISSING_PARAMETER_ERR_MSG.value, "header"
                        )
                    )
                )
            ),
            responseClass = CreateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }


    private fun missingRequestBodyScenario(): TestScenario<String, CreateUserResponse> {
        val preScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        val postScenario: () -> Unit = {
            Assert.assertFalse(this.userRepository.findByEmail(email).isPresent)
            Assert.assertFalse(this.userRepository.findByPhoneNumber(phoneNumber).isPresent)
        }

        return TestScenario(
            description = "Missing request body scenario",
            endpoint = this.endpoint,
            httpHeaders = this.httpHeaders,
            requestBody = null,
            expectedHttpStatus = HttpStatus.BAD_REQUEST,
            expectedResponseBody = CreateUserResponse(
                header = Header(
                    messageId = UUID.randomUUID().toString(),
                    timestamp = OffsetDateTime.now(),
                    responseStatus = Status(
                        status = ResponseStatus.REJECTED.value,
                        errorCode = ApiResponseHelper.lookupErrorCode(ErrorCodes.INVALID_REQUEST_ERR.value),
                        errorMessage = ApiResponseHelper.lookupErrorMessage(ErrorCodes.INVALID_REQUEST_BODY_ERR_MSG.value)
                    )
                )
            ),
            responseClass = CreateUserResponse::class.java,
            preScenario = preScenario,
            postScenario = postScenario
        )
    }
}