package io.skambo.example.application.helpers

import org.junit.Assert
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.*


class SortingAndPaginationHelperTest {
    @Test
    fun testCreatePageRequest(){
        // This is the test data
        val pageNumber: Int = 3
        val pageSize: Int = 20
        val sortDirection: String = "asc"
        val sortFields: List<String> = listOf("name", "age", "city")

        // We are creating the expected response for assertion
        val direction: Sort.Direction = Sort.Direction.ASC

        // We're iterating through every item in the ordered list
        val orders: List<Sort.Order> = sortFields.map { field -> Sort.Order(direction, field) }.toList()
        val sort: Sort = Sort.by(orders)
        val expectedResponse: PageRequest = PageRequest.of(pageNumber, pageSize, sort)

        // Here, we are invoking the method and get its response
        val actualResponse: PageRequest = SortingAndPaginationHelper.createPageRequest(pageNumber, pageSize, sortDirection, sortFields)

        // Now we do the assertion
        Assert.assertEquals(expectedResponse, actualResponse)
    }
}