package io.skambo.example.infrastructure.persistence.jpa.repositories

import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CrudRepository<UserDataModel, Long> {
}