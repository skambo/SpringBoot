package io.skambo.example.infrastructure.persistence.jpa.entities

import javax.persistence.*

@Entity(name = "user")
data class UserDataModel (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 256)
    val name: String,

    @Column(nullable = false)
    val dateOfBirth: String,

    @Column(nullable = false, length = 256)
    val city: String,

    @Column(nullable = false, length = 100, unique = true)
    val email: String,

    @Column(nullable = false, length = 100, unique = true)
    val phoneNumber: String
)