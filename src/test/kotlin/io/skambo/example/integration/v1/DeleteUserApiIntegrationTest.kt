package io.skambo.example.integration.v1

import io.skambo.example.infrastructure.api.deleteuser.v1.dto.DeleteUserResponse
import io.skambo.example.integration.BaseApiIntegrationTest
import io.skambo.example.integration.utils.TestScenario
import org.springframework.http.HttpMethod

class DeleteUserApiIntegrationTest: BaseApiIntegrationTest<Unit, DeleteUserResponse>() {
    override val url: String = "/api/v1/deleteUser"

    override val httpMethod: HttpMethod = HttpMethod.DELETE

    override val requestBody: Unit? = null

    override fun createTestScenarios(): List<TestScenario<Unit, DeleteUserResponse>> {
        return listOf()
    }
}