package io.skambo.example.application.services

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.model.User
import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

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

    fun findUsers(pageRequest:PageRequest, specification: Specification<UserDataModel>?):Page<User>{

        val users = userRepository.findAll(pageable = pageRequest, specification = specification)
        return users.map {userDataModel -> userDataModelToUser(userDataModel)}
    }

    private fun userDataModelToUser(userDataModel: UserDataModel): User{
        return User(
                id = userDataModel.id,
                name = userDataModel.name,
                dateOfBirth = OffsetDateTime.parse(userDataModel.dateOfBirth),
                city = userDataModel.city,
                email = userDataModel.email,
                phoneNumber = userDataModel.phoneNumber
        )
    }
}