package io.skambo.example.infrastructure.persistence.jpa.repositories

import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CrudRepository<UserDataModel, Long> {
    @Query(value = "SELECT UserDataModel from user u WHERE u.email = email")
    fun findByEmail(email:String): Optional<UserDataModel>
    fun findByPhoneNumber(phoneNumber:String): Optional<UserDataModel>
}