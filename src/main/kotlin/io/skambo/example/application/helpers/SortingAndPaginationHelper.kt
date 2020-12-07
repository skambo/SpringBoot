package io.skambo.example.application.helpers

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

class SortingAndPaginationHelper {
    companion object {
        fun createPageRequest(pageNumber: Int, pageSize:Int, sortDirection:String, sortFields:List<String>) : PageRequest{
            val sort: Sort = Sort.by(sortFields[0])
            for (i in 1 until sortFields.size) {
                sort.and(Sort.by(sortFields[i]))
            }

            if(sortDirection == "desc"){
                sort.descending()
            } else {
                sort.ascending()
            }
            return PageRequest.of(pageNumber, pageSize, sort)
        }
    }
}