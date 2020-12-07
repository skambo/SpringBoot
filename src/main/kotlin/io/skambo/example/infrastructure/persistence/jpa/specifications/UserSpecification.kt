package io.skambo.example.infrastructure.persistence.jpa.specifications

import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import org.springframework.data.jpa.domain.Specification
import java.lang.reflect.Array.get
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root


class UserSpecification(private val criteria: FilterCriteria): Specification<UserDataModel> {

    override fun toPredicate(root: Root<UserDataModel?>, query: CriteriaQuery<*>?, builder: CriteriaBuilder): Predicate? {
        if (criteria.operation.equals(">", ignoreCase = true)) {
            return builder.greaterThanOrEqualTo(
                root.get<String>(criteria.key), criteria.value.toString()
            )
        } else if (criteria.operation.equals("<", ignoreCase = true)) {
            return builder.lessThanOrEqualTo(
                root.get<String>(criteria.key), criteria.value.toString()
            )
        } else if (criteria.operation.equals(":",  ignoreCase = true)) {
            return if (root.get<String>(criteria.key).javaType === String::class.java) {
                builder.like(
                    root.get<String>(criteria.key),
                    "%" + criteria.value.toString() + "%"
                )
            } else {
                builder.equal(root.get<String>(criteria.key), criteria.value.toString())
            }
        }
        return null
    }
}