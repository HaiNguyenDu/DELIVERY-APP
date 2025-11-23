package com.example.grabapp.domain.use_case

import com.example.grabapp.domain.UserRepository
import com.example.grabapp.domain.model.user.User

class GetUserUseCase(private val repo: UserRepository) {
    operator fun invoke() = repo.getUser()
}

class InsertUserUseCase(private val repo: UserRepository){
    suspend operator fun invoke(user: User) = repo.insertUser(user)
}