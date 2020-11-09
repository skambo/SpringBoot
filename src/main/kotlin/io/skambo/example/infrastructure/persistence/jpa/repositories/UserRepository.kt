package io.skambo.example.infrastructure.persistence.jpa.repositories

import io.skambo.example.infrastructure.persistence.jpa.entities.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
}