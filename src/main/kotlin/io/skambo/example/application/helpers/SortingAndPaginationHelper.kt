package io.skambo.example.application.helpers

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class SortingAndPaginationHelper {
    companion object {
        fun createPageRequest(pageNumber: Int, pageSize:Int, sortDirection:String, sortFields:List<String>) : PageRequest{
            var direction: Sort.Direction = Sort.Direction.ASC

            if(sortDirection == "desc"){
                direction = Sort.Direction.DESC
            }
            val orders : List<Sort.Order> = sortFields.map { field -> Sort.Order(direction, field) }.toList()
            val sort: Sort = Sort.by(orders)
            return PageRequest.of(pageNumber, pageSize, sort)
        }
    }
}