package iss.nus.edu.sg.memory_game.dao

data class LoginResponse(
    val isAuthenticated: Boolean,
    val isPaidUser: Boolean,
    val message : String? = null
)
