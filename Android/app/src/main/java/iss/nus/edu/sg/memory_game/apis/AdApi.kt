package iss.nus.edu.sg.memory_game.apis

import retrofit2.http.GET

interface AdApi {
    @GET("api/ad")
    suspend fun getAdUrls(): List<String>
}
