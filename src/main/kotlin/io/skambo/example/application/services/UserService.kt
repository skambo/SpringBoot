package io.skambo.example.application.services

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.model.User
import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.sql.SQLIntegrityConstraintViolationException

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
        if (userRepository.findByEmail(user.email).isPresent()){
           //throw a custom domain exception
            throw DuplicateUserException("User with email = ${user.email} exists")
        }

        if (userRepository.findByPhoneNumber(user.phoneNumber).isPresent()){
            //throw a custom domain exception
            throw DuplicateUserException("User with phoneNumber = ${user.phoneNumber} exists")
        }

        val id: Long = userRepository.save(userDataModel).id!!
        user.id = id
        return user
    }
}