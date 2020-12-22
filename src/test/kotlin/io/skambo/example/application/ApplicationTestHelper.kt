package io.skambo.example.application

import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import io.skambo.example.infrastructure.persistence.jpa.specifications.EntitySpecificationBuilder
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import java.util.regex.Matcher
import java.util.regex.Pattern

object ApplicationTestHelper {
    fun createTestPageRequest(
        pageNumber:Int = 3,
        pageSize:Int = 20,
        sortDirection:String = "asc",
        sortFields:List<String> = listOf("name", "age", "city")
        ):PageRequest {

        // We are creating the expected response for assertion
        var direction: Sort.Direction = Sort.Direction.ASC
        if (sortDirection == "desc"){
            direction = Sort.Direction.DESC
        }

        // We're iterating through every item in the ordered list
        val orders: List<Sort.Order> = sortFields.map { field -> Sort.Order(direction, field) }.toList()
        val sort: Sort = Sort.by(orders)

        return PageRequest.of(pageNumber, pageSize, sort)
    }

    fun createTestSpecification(filters:String = ""):Specification<UserDataModel>? {
        val builder = EntitySpecificationBuilder<UserDataModel>()
        val pattern: Pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),")
        val matcher: Matcher = pattern.matcher("$filters,")
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3))
        }

        return builder.build()
    }
}