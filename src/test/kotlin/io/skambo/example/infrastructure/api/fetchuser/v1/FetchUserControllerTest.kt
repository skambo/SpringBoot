package io.skambo.example.infrastructure.api.fetchuser.v1

import io.skambo.example.application.services.UserService
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.servlet.http.HttpServletRequest

@ExtendWith(SpringExtension::class)
class FetchUserControllerTest {
    @MockBean
    private lateinit var mockUserService: UserService

    @Mock
    private lateinit var testHttpServletRequest: HttpServletRequest

    private lateinit var testFetchUserController: FetchUserController

}