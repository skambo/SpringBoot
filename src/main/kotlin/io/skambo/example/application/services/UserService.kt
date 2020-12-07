package io.skambo.example.application.services

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.model.User
import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import io.skambo.example.infrastructure.persistence.jpa.specifications.EntitySpecificationBuilder
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.regex.Matcher
import java.util.regex.Pattern

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

    fun findUsers(pageRequest:PageRequest, filters: String):Page<User>{
        val builder = EntitySpecificationBuilder<UserDataModel>()
        val pattern: Pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),")
        val matcher: Matcher = pattern.matcher("$filters,")
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3))
        }

        val specification: Specification<UserDataModel>? = builder.build()

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