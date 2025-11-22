package com.example.grabapp.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val phone: String,
    val fullName: String,
    val avatarUrl: String?,
    val isActive: Boolean,
    val role: Int,
    val date: String?,
    val isAvailable: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val deletedAt: Long?
)