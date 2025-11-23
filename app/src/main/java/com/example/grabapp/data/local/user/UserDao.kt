package com.example.grabapp.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getUser(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM user_addresses LIMIT 1")
    fun getAddresses(): Flow<UserAddressEntity?>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAddresses(addresses: UserAddressEntity)
}