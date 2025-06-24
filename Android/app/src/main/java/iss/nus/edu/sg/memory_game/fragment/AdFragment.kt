package iss.nus.edu.sg.memory_game.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import kotlin.random.Random
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import iss.nus.edu.sg.memory_game.R

class AdFragment : Fragment() {
    private lateinit var adTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var adRunnable: Runnable
    private val isPaidUser = false

    val adMessages = listOf(
        "🎮 Try our new game today!",
        "🔥 50% off on all features!",
        "🚀 Upgrade to Premium now!",
        "🎁 Get daily rewards — Sign up!",
        "📣 New features rolling out!"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_ad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adTextView = view.findViewById(R.id.adTextView)

        if(isPaidUser) {
            adTextView.visibility = View.GONE
            return
        }

        showRandomAd() // initial load

        adRunnable = object : Runnable {
            override fun run() {
                showRandomAd()
                handler.postDelayed(this, 2_000)
            }
        }

        handler.postDelayed(adRunnable, 2_000)
    }

    private fun showRandomAd() {
        val isSuccess = Random.nextBoolean()

        if (isSuccess) {
            val selectedAd = adMessages.random()
            adTextView.text = selectedAd
        } else {
            adTextView.text = "Failed to load ad. Please try again."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(adRunnable)
    }
}