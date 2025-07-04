package iss.nus.edu.sg.memory_game.apis

import iss.nus.edu.sg.memory_game.dao.LoginRequest
import iss.nus.edu.sg.memory_game.dao.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

//to implement login function
interface AuthApi {
    @POST("api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

}