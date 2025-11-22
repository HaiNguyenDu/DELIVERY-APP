package com.example.grabapp.domain.model.user

import com.example.grabapp.data.local.user.UserAddressEntity
import com.example.grabapp.data.local.user.UserEntity

fun UserEntity.toDomain() = User(id, phone, fullName, avatarUrl, isActive, role, date, isAvailable)

fun User.toEntity(): UserEntity {
    val now = System.currentTimeMillis()
    return UserEntity(
        id = id,
        phone = phone,
        fullName = fullName,
        avatarUrl = avatarUrl,
        isActive = isActive,
        role = role,
        date = date,
        isAvailable = isAvailable,
        createdAt = now,
        updatedAt = now,
        deletedAt = null
    )
}

fun UserAddressEntity.toDomain() =
    UserAddress(id, userId, detail, phone, latitude, longitude, isDefault)

fun UserAddress.toEntity(): UserAddressEntity {
    val now = System.currentTimeMillis()
    return UserAddressEntity(
        id = id,
        userId = userId,
        detail = detail,
        phone = phone,
        latitude = latitude,
        longitude = longitude,
        isDefault = isDefault,
        createdAt = now,
        updatedAt = now
    )

}

