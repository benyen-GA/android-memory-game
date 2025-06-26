package iss.nus.edu.sg.memory_game.fragment

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import iss.nus.edu.sg.memory_game.R
import java.io.File
import java.io.FileOutputStream

class PlayFragment : Fragment() {
    private lateinit var cardGrid: GridLayout
    private lateinit var imagePathList: List<String>
    private lateinit var matchCounter: TextView
    private lateinit var timer: TextView
    private lateinit var bestTime: TextView

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
    private var mediaPlayer: MediaPlayer?=null
    private var bgmPlayer: MediaPlayer? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_play, container, false)
        cardGrid = view.findViewById(R.id.cardGrid)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        matchCounter = view.findViewById(R.id.matchCounter)
        timer = view.findViewById(R.id.timer)

        bestTime = view.findViewById(R.id.bestTimeTextView)
        loadBestTime()
        startBGM()

        imagePathList = getImagePaths()

        matchedCount = 0
        gameStarted = false
        updateMatchCounter()
        updateTimerText(0)

        showCardBacks()

        childFragmentManager.beginTransaction().replace(R.id.adView, AdFragment()).commit()
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
                    setMargins(8,8,8,8)
                }
                dealcard(this,i)
                setOnClickListener {
                    if (!gameStarted){
                        gameStarted = true
                        startTimer()
                    }

                    if (isFlipping || this.tag == "matched" || this == firstCard) {
                        playSoundEffect(false)
                        return@setOnClickListener
                    }
                    playSoundEffect(true)
                    flipcard(this, showFront = true, frontImgPath = imgPath)

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
                firstCard?.let {
                    markedAsMatched(it)
                    playMatchAnimation(it)
                }
                secondCard?.let {
                    markedAsMatched(it)
                    playMatchAnimation(it)
                }

                firstCard = null
                secondCard = null
                firstImgPath = null
                isFlipping = false

                if (matchedCount == 6){
                    stopTimer()
                    Toast.makeText(context,"All matched! Congratulation!",Toast.LENGTH_SHORT).show()
                    playMusic(R.raw.win)
                    saveBestTimeIfNeeded(seconds)
                    cardGrid.postDelayed({
                        view?.findNavController()?.navigate(R.id.action_play_to_leaderboard)
                    }, 1000)
                }
            } else {
                cardGrid.postDelayed({
                    playMusic(R.raw.flip_back)
                    flipcard(firstCard!!, showFront = false)
                    flipcard(secondCard!!, showFront = false)
                    firstCard = null
                    secondCard = null
                    firstImgPath = null
                    isFlipping = false
                }, 1000)
            }
        }
    }

    private fun loadBestTime() {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val best = sharedPref.getInt("BEST_TIME", Int.MAX_VALUE)
        if (best != Int.MAX_VALUE) {
            bestTime.text = "Best Time: ${formatTime(best)}"
        }
    }

    private fun saveBestTimeIfNeeded(current: Int) {
        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val best = sharedPref.getInt("BEST_TIME", Int.MAX_VALUE)
        if (current < best) {
            sharedPref.edit().putInt("BEST_TIME", current).apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopMusic()
        stopBGM()
    }

    private fun markedAsMatched(imageView: ImageView) {
        imageView.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
        imageView.animate()
            .alpha(0.6f)
            .setDuration(450)
            .start()
    }
    private fun dealcard(imageView: ImageView,index:Int){
        imageView.alpha=0f
        imageView.animate()
            .alpha(1f)
            .setStartDelay((index * 100).toLong())
            .setDuration(300)
            .start()
    }
    private fun flipcard(
        card: ImageView,
        showFront: Boolean,
        frontImgPath: String? = null){
        card.animate()
            .rotationY(90f)
            .setDuration(150)
            .withEndAction {
                if (showFront) {
                    val bitmap = BitmapFactory.decodeFile(frontImgPath)
                    card.setImageBitmap(bitmap)
                } else {
                    card.setImageResource(R.drawable.card_back)
                }
                card.rotationY = 90f
                card.animate()
                    .rotationY(180f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }

    private fun playSoundEffect(flip:Boolean){
        val soundId = if (flip) R.raw.flip else R.raw.error
        playMusic(soundId)
    }
    private fun playMatchAnimation(imageView: ImageView){
        imageView.animate()
            .scaleX(1.2f).scaleY(1.2f)
            .setDuration(100)
            .withEndAction {
                imageView.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(100)
                    .start()
            }
        playMusic(R.raw.match_music)
    }

    private fun startBGM() {
        if (bgmPlayer == null) {
            bgmPlayer = MediaPlayer.create(requireContext(), R.raw.bgm)
            bgmPlayer?.isLooping = true
            bgmPlayer?.start()
        }
    }
    private fun stopBGM() {
        bgmPlayer?.stop()
        bgmPlayer?.release()
        bgmPlayer = null
    }
    private fun playMusic(index:Int){
        mediaPlayer?.release()
        mediaPlayer= MediaPlayer.create(requireContext(),index).apply{
                setOnCompletionListener { release() }
            start()
        }
    }

    private fun updateMatchCounter(){
        matchCounter.text = "$matchedCount / 6 matches"
    }
    private fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }
    private fun updateTimerText(seconds: Int){
        var timerFormat = formatTime(seconds)
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

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
