package io.skambo.example.infrastructure.persistence.jpa.specifications

data class FilterCriteria(
    val key: String,
    val operation: String,
    val value: Any
) {

}