package com.example.grabapp.data.local.user

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_addresses",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserAddressEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val detail: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val isDefault: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)