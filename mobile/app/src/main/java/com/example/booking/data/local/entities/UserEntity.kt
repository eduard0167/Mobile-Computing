package com.example.booking.data.local.entities

import androidx.room.PrimaryKey
import androidx.room.Entity

enum class Role {
    STUDENT,
    ADMIN
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val faculty: String,
    val year: String,
    val role: Role,
)
