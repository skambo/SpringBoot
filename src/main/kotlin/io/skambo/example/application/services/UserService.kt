package io.skambo.example.application.services

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.model.User
import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import kotlin.streams.toList

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

    fun findUsers(pageSize:Int, sortField: String, sortDirection:String):List<User>{
        val sort: Sort = Sort.by(sortField)

        if(sortDirection == "desc"){
            sort.descending()
        } else {
            sort.ascending()
        }
        val pageRequest: PageRequest = PageRequest.of(0, pageSize, sort)
        val users = userRepository.findAll(pageable = pageRequest)
        // this is how to transform data using the streams API
        return users
            .stream()
            .map {userDataModel -> userDataModelToUser(userDataModel)}
            .toList()
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