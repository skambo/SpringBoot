package io.skambo.example.infrastructure.persistence.jpa.entities

import javax.persistence.*

@Entity
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false, length = 256)
    val name: String,

    @Column(nullable = false)
    val age: Int,

    @Column(nullable = false, length = 256)
    val city: String,

    @Column(nullable = false, length = 256)
    val email: String,

    @Column(nullable = false, length = 256)
    val phoneNumber: String
)