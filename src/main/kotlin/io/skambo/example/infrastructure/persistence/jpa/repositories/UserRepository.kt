package io.skambo.example.infrastructure.persistence.jpa.repositories

import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CrudRepository<UserDataModel, Long>, JpaSpecificationExecutor<UserDataModel> {
    //@Query(value = "SELECT UserDataModel from user u WHERE u.email = email")
    fun findByEmail(email:String): Optional<UserDataModel>
    fun findByPhoneNumber(phoneNumber:String): Optional<UserDataModel>
    override fun findAll(specification: Specification<UserDataModel>?, pageable: Pageable): Page<UserDataModel>
}