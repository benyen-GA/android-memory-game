package iss.nus.edu.sg.memory_game.fragment

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import iss.nus.edu.sg.memory_game.R
import java.io.File

class PlayFragment : Fragment() {
    private lateinit var cardGrid: GridLayout
    private lateinit var imagePathList: List<String>
    private lateinit var matchCounter: TextView
    private lateinit var timer: TextView


    private var imageList: List<String> = listOf()
    private var firstCard: ImageView? = null
    private var secondCard: ImageView? = null
    private var firstImgPath: String? = null
    private var matchedCount = 0
    private var isFlipping = false
    private var gameStarted = false
    private var seconds = 0
    private var runningTimer = false
    private val handler = Handler(Looper.getMainLooper())
    private val runTimer = object : Runnable{
        override fun run() {
            if (runningTimer){
                seconds++
                updateTimerText(seconds)
                handler.postDelayed(this, 1000)
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_play, container, false)
        cardGrid = view.findViewById(R.id.cardGrid)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginPrefs = requireActivity().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val isPaidUser = loginPrefs.getBoolean("isPaidUser", false)

        matchCounter = view.findViewById(R.id.matchCounter)
        timer = view.findViewById(R.id.timer)

        imagePathList = getImagePaths()

        matchedCount = 0
        gameStarted = false
        updateMatchCounter()
        updateTimerText(0)

        showCardBacks()

        if (!isPaidUser) {
            childFragmentManager.beginTransaction().replace(R.id.adView, AdFragment()).commit()
        } else {
            view.findViewById<View>(R.id.adView)?.visibility = View.INVISIBLE
        }
    }

    private fun showCardBacks(){
        cardGrid.removeAllViews()

        val columns = 3

        imageList = (imagePathList + imagePathList).shuffled()

        for (i in 0..11){
            val imgPath = imageList[i]
            val card = ImageView(requireContext()).apply {
                setImageResource(R.drawable.card_back)
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    rowSpec = GridLayout.spec(i / columns, 1f)
                    columnSpec = GridLayout.spec(i % columns, 1f)
                    setMargins(5,5,5,5)
                }
                setOnClickListener {
                    if (!gameStarted){
                        gameStarted = true
                        startTimer()
                    }

                    if (isFlipping || this.tag == "matched" || this == firstCard) {
                        return@setOnClickListener
                    }

                    val bitmap = BitmapFactory.decodeFile(imgPath)
                    setImageBitmap(bitmap)

                    flipLogic(this, imgPath)
                }
            }
            cardGrid.addView(card)
        }
    }

    private fun getImagePaths(): List<String> {
        val context = requireContext()
        return (1..6).map { index ->
            File(context.cacheDir, "image_$index.jpg").absolutePath
        }
    }

    private fun flipLogic(currentCard: ImageView, imgPath: String){
        if (firstCard == null){
            firstCard = currentCard
            firstImgPath = imgPath
        } else if (secondCard == null){
            secondCard = currentCard
            isFlipping = true

            if (firstImgPath == imgPath){
                firstCard?.tag = "matched"
                secondCard?.tag = "matched"
                matchedCount ++
                updateMatchCounter()
                firstCard?.let { markedAsMatched(it) }
                secondCard?.let { markedAsMatched(it) }

                firstCard = null
                secondCard = null
                firstImgPath = null
                isFlipping = false

                if (matchedCount == 6){
                    stopTimer()
                    Toast.makeText(context,"All matched! Congratulation!",Toast.LENGTH_SHORT).show()
                    cardGrid.postDelayed({
                        view?.findNavController()?.navigate(R.id.action_play_to_leaderboard)
                    }, 1000)
                }
            } else {
                cardGrid.postDelayed({
                    firstCard?.setImageResource(R.drawable.card_back)
                    secondCard?.setImageResource(R.drawable.card_back)
                    firstCard = null
                    secondCard = null
                    firstImgPath = null
                    isFlipping = false
                }, 1000)
            }
        }
    }

    private fun markedAsMatched(imageView: ImageView) {
        imageView.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
        imageView.animate()
            .alpha(0.6f)
            .setDuration(450)
            .start()
    }

    private fun updateMatchCounter(){
        matchCounter.text = "$matchedCount / 6 matches"
    }

    private fun updateTimerText(seconds: Int){
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val second = seconds % 60
        val timerFormat = String.format("%02d:%02d:%02d", hours, minutes, second)

        timer.text = "$timerFormat"
    }

    private fun startTimer(){
        if (!runningTimer){
            runningTimer = true
            handler.post(runTimer)
        }
    }

    private fun stopTimer(){
        runningTimer = false
        handler.removeCallbacks(runTimer)
    }

}
