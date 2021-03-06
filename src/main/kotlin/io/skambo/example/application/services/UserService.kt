package io.skambo.example.application.services

import io.skambo.example.application.domain.exceptions.DuplicateUserException
import io.skambo.example.application.domain.exceptions.UserNotFoundException
import io.skambo.example.application.domain.model.User
import io.skambo.example.infrastructure.persistence.jpa.entities.UserDataModel
import io.skambo.example.infrastructure.persistence.jpa.repositories.UserRepository
import io.skambo.example.infrastructure.persistence.jpa.specifications.EntitySpecificationBuilder
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@Service
class UserService(
    private val userRepository: UserRepository,
    private val kafkaTemplate: KafkaTemplate<String, User>
) {
    private val LOGGER = LoggerFactory.getLogger(UserService::class.java)

    @Throws(DuplicateUserException::class)
    fun createUser(user: User) : User {
        checkUserUniqueness(user.email, user.phoneNumber)
        val userDataModel: UserDataModel = UserDataModel(
                name = user.name,
                dateOfBirth = user.dateOfBirth.toString(),
                city = user.city,
                email = user.email,
                phoneNumber = user.phoneNumber
        )

        val id: Long = userRepository.save(userDataModel).id!!
        user.id = id
        kafkaTemplate.send("Kafka_Example", user)

        MDC.put("userDetails", user.toString())
        LOGGER.info("User created")
        MDC.clear()

        return user
    }

    fun updateUser(user:User) {
        // checkUserUniqueness(user.email, user.phoneNumber)
        val userDataModel: UserDataModel = UserDataModel(
            id = user.id,
            name = user.name,
            dateOfBirth = user.dateOfBirth.toString(),
            city = user.city,
            email = user.email,
            phoneNumber = user.phoneNumber
        )
        userRepository.save(userDataModel)

        MDC.put("userDetails", user.toString())
        LOGGER.info("User updated")
        MDC.clear()
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

        LOGGER.info("${users.size} users found")

        return users.map {userDataModel -> userDataModelToUser(userDataModel)}
    }

    @Throws(UserNotFoundException::class)
    fun findUserById(userId:String): User {
        try{
            val userIdLong: Long = userId.toLong()
            val userDataModel:UserDataModel = findUserDataModelById(userIdLong)

            MDC.put("userId", userId)
            LOGGER.info("User found")
            MDC.clear()

            return userDataModelToUser(userDataModel)
        } catch(numberFormatException: NumberFormatException){
            throw UserNotFoundException(message = "User with id '$userId' not found", cause = numberFormatException)
        }
    }

    @Throws(UserNotFoundException::class)
    fun deleteUser(userId:String){
        try{
            val userIdLong: Long = userId.toLong()
            val userDataModel:UserDataModel = findUserDataModelById(userIdLong)
            userRepository.delete(userDataModel)

            MDC.put("userId", userId)
            LOGGER.info("User deleted")
            MDC.clear()

        } catch(numberFormatException: NumberFormatException){
            throw UserNotFoundException(message = "User with id '$userId' not found", cause = numberFormatException)
        }
    }

    private fun findUserDataModelById(userId:Long) : UserDataModel {
        val userDataModelOptional:Optional<UserDataModel> = userRepository.findById(userId)
        if(userDataModelOptional.isEmpty){
            throw UserNotFoundException("User with id $userId not found")
        }
        return userDataModelOptional.get()
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

    private fun checkUserUniqueness(email:String, phoneNumber:String){
        if (userRepository.findByEmail(email).isPresent){
            LOGGER.info("User with email address $email exists")
            //throw a custom domain exception
            throw DuplicateUserException("User with email = $email exists")
        }

        if (userRepository.findByPhoneNumber(phoneNumber).isPresent){
            LOGGER.info("User with phone number $phoneNumber exists")
            //throw a custom domain exception
            throw DuplicateUserException("User with phoneNumber = $phoneNumber exists")
       }
    }
}