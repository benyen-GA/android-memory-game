package iss.nus.edu.sg.memory_game.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.memory_game.R
import iss.nus.edu.sg.memory_game.adapter.LeaderBoardAdapter
import iss.nus.edu.sg.memory_game.apis.RetrofitClient
import iss.nus.edu.sg.memory_game.dao.ScoreResult
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class LeaderboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LeaderBoardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentScore = arguments?.getInt("score") ?: 0
        val minutes = currentScore / 60
        //remainder for seconds
        val seconds = currentScore % 60

        //formatting time into MM:SS format
        val formattedTime = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        view.findViewById<TextView>(R.id.yourScoreText).text = formattedTime

        recyclerView = view.findViewById(R.id.scoreRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<Button>(R.id.closeButton)?.setOnClickListener {
            view.findNavController().navigate(R.id.action_leaderboard_to_fetch)
        }

        fetchTopScores()
    }

    //sending req to ASP.NET server to fetch top 5 scores
    private fun fetchTopScores() {
        RetrofitClient.scoreApi.getTopFive().enqueue(object : Callback<List<ScoreResult>> {
            override fun onResponse(call: Call<List<ScoreResult>>, response: Response<List<ScoreResult>>) {
                if (response.isSuccessful) {
                    //if successful, to retrieve response body else it will return an empty list
                    val scores = response.body() ?: emptyList()
                    adapter = LeaderBoardAdapter(scores)
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(context, "Failed to load scores", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ScoreResult>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

