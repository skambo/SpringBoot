package io.skambo.example.application.helpers

import io.skambo.example.application.ApplicationTestHelper
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest


class SortingAndPaginationHelperTest {
    @Test
    fun testCreatePageRequest(){
        //TODO Improve this test with property based testing

        // This is the test data
        val pageNumber: Int = 1
        val pageSize: Int = 25
        val sortDirection: String = "desc"
        val sortFields: List<String> = listOf("name", "age", "city")

        val expectedResponse: PageRequest = ApplicationTestHelper.createTestPageRequest(pageNumber, pageSize, sortDirection, sortFields)

        // Here, we are invoking the method and get its response
        val actualResponse: PageRequest = SortingAndPaginationHelper.createPageRequest(pageNumber, pageSize, sortDirection, sortFields)

        // Now we do the assertion
        Assert.assertEquals(expectedResponse, actualResponse)
    }
}