package iss.nus.edu.sg.memory_game.apis

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//singleton will only have 1 instance of client
object RetrofitClient {
    private const val url = "http://10.0.2.2:5053/"

    // create AuthAPI only when it is being used, then cache it after. (lazy initialisation)
    val instance: AuthApi by lazy {
        //instantiating retrofit object
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //creates an implementation of AuthApi interface
        retrofit.create(AuthApi::class.java)
    }

    //LST: for scoreAPI
    val scoreApi: ScoreApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ScoreApi::class.java)
    }

    val adApi: AdApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(AdApi::class.java)
    }
}