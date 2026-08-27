package com.example.javatokotlin

import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun createUser(name: String, email: String): User {
        val user = User()
        user.setName(name)
        user.setEmail(email)

        return userRepository.save(user)
    }

    fun getUserById(id: Long): Optional<User> {
        return userRepository.findById(id)
    }

    fun updateUsername(id: Long, updateName: String): User {
        val user = userRepository.findById(id)
            .orElseThrow { RuntimeException("Not found User") }

        user.setName(updateName)

        return userRepository.save(user)
    }
}
