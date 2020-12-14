package io.skambo.example.infrastructure.persistence.jpa.specifications

import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import org.springframework.data.jpa.domain.Specification
import java.lang.reflect.Array.get
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root


class EntitySpecification<Entity>(private val criteria: FilterCriteria): Specification<Entity> {
    override fun toPredicate(
        root: Root<Entity>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {

        if (criteria.operation.equals(">", ignoreCase = true)) {
            return criteriaBuilder.greaterThanOrEqualTo(
                root.get<String>(criteria.key), criteria.value.toString()
            )
        } else if (criteria.operation.equals("<", ignoreCase = true)) {
            return criteriaBuilder.lessThanOrEqualTo(
                root.get<String>(criteria.key), criteria.value.toString()
            )
        } else if (criteria.operation.equals(":",  ignoreCase = true)) {
            return if (root.get<String>(criteria.key).javaType === String::class.java) {
                criteriaBuilder.like(
                    root.get<String>(criteria.key),
                    "%" + criteria.value.toString() + "%"
                )
            } else {
                criteriaBuilder.equal(root.get<String>(criteria.key), criteria.value.toString())
            }
        }
        return null
    }
}