package io.skambo.example.application.services

import io.skambo.example.application.domain.model.User
import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
    fun create(user: User) : User {
        val userDataModel: UserDataModel = UserDataModel(
                name = user.name,
                dateOfBirth = user.dateOfBirth.toString(),
                city = user.city,
                email = user.email,
                phoneNumber = user.phoneNumber
        )
        val id: Long = userRepository.save(userDataModel).id!!
        user.id = id
        return user
    }
}