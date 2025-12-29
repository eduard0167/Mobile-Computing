package com.example.booking.data.repository

import com.example.booking.data.local.entities.UserEntity
import com.example.booking.data.local.entities.Role
import com.example.booking.data.remote.model.UserDto

fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        phoneNumber = this.phoneNumber ?: "",
        faculty = this.faculty ?: "",
        year = this.year ?: "",
        role = Role.valueOf(this.role.uppercase())
    )
}
