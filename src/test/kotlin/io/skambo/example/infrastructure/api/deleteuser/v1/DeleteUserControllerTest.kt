package io.skambo.example.infrastructure.api.deleteuser.v1

import io.skambo.example.application.domain.model.User
import io.skambo.example.application.services.UserService
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import javax.servlet.http.HttpServletRequest

@ExtendWith(SpringExtension::class)
class DeleteUserControllerTest {
    @MockBean
    private lateinit var mockUserService: UserService

    @Mock
    private lateinit var testHttpServletRequest: HttpServletRequest

    private lateinit var testDeleteUserController: DeleteUserController

    private val mockUser: User = User(
        id = 1L,
        name = "Anne",
        dateOfBirth = OffsetDateTime.now(),
        city = "Nairobi",
        email = "anne@gmail.com",
        phoneNumber = "1224"
    )
}