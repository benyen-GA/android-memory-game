package iss.nus.edu.sg.memory_game.apis

import retrofit2.Call
import iss.nus.edu.sg.memory_game.dao.ScoreRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ScoreApi {
    @POST("api/score1/add")
    fun addScore(@Body scoreRequest: ScoreRequest): Call<Void>
}