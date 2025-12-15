package com.example.grabapp.domain.model.user

import com.example.grabapp.data.local.user.UserAddressEntity
import com.example.grabapp.data.local.user.UserEntity
import com.example.grabapp.data.model.user.UserResponse

fun UserEntity.toDomain() = User(id, phone, fullName, avatarUrl, isActive, emptyList(), date, isAvailable)

fun User.toEntity(): UserEntity {
    val now = System.currentTimeMillis()
    return UserEntity(
        id = id,
        phone = phone,
        fullName = fullName,
        avatarUrl = avatarUrl,
        isActive = isActive,
        role = 1,
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

fun UserResponse.toUser(): User {
    return User(
        id = id,
        phone = phone,
        fullName = fullName,
        avatarUrl = avatarUrl,
        isActive = isActive,
        role = roles,
        date = dob,
        isAvailable = enabled
    )
}


