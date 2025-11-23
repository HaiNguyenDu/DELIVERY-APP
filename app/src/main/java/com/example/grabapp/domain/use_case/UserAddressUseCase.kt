package com.example.grabapp.domain.use_case

import com.example.grabapp.domain.UserRepository

class getUserAddress(private val repo: UserRepository){
    operator fun invoke() = repo.getUserAddress()
}
