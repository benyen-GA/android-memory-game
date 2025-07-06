package iss.nus.edu.sg.memory_game.adapter


import android.view.LayoutInflater
import android.view.View
import iss.nus.edu.sg.memory_game.R
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.memory_game.dao.ScoreResult

class LeaderBoardAdapter(private val items: List<ScoreResult>) : RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usernameText = view.findViewById<TextView>(R.id.usernameText)
        val timeText = view.findViewById<TextView>(R.id.timeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_score_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //getting individual score based on the position
        val score = items[position]
        val minutes = score.time / 60
        //remainder for seconds
        val seconds = score.time % 60

        //formatting time into MM:SS format
        val formattedTime = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"

        holder.usernameText.text = score.username
        holder.timeText.text = formattedTime
    }

    override fun getItemCount() = items.size

}