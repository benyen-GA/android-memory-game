package iss.nus.edu.sg.memory_game.dao

object TempUserDAO {
    private val users = listOf(TempUser("freeUser", "free", false), TempUser("paidUser", "paid", true))

    fun authenticate(username: String, password: String) : TempUser? {
        val user = users.find { it.username == username && it.password == password }
        return user
    }
}