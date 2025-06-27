package iss.nus.edu.sg.memory_game.fragment

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.media.SoundPool
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import iss.nus.edu.sg.memory_game.R
import java.io.File
import java.io.FileOutputStream
import android.util.Log

class PlayFragment : Fragment() {
    private lateinit var cardGrid: GridLayout
    private lateinit var imagePathList: List<String>
    private lateinit var matchCounter: TextView
    private lateinit var timer: TextView
    private lateinit var bestTime: TextView
    private lateinit var soundPool: SoundPool

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
    private val soundMap = mutableMapOf<Int, Int>()


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

        bestTime = view.findViewById(R.id.bestTimeTextView)
        loadBestTime()
        startBGM()

        imagePathList = getImagePaths()

        soundPool = SoundPool.Builder().setMaxStreams(5).build()
        soundMap[R.raw.flip] = soundPool.load(requireContext(), R.raw.flip, 1)
        soundMap[R.raw.error] = soundPool.load(requireContext(), R.raw.error, 1)
        soundMap[R.raw.match_music] = soundPool.load(requireContext(), R.raw.match_music, 1)
        soundMap[R.raw.flip_back] = soundPool.load(requireContext(), R.raw.flip_back, 1)
        soundMap[R.raw.win] = soundPool.load(requireContext(), R.raw.win, 1)

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
                    isFlipping = true
                    Log.d("Playfragment", "ImgPath = $imgPath")
                    flipcard(this, showFront = true, frontImgPath = imgPath){
                        flipLogic(this, imgPath)
                    }
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
            isFlipping = false
        } else if (secondCard == null){
            secondCard = currentCard

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
                    playSound(R.raw.win)
                    saveBestTimeIfNeeded(seconds)
                    cardGrid.postDelayed({
                        view?.findNavController()?.navigate(R.id.action_play_to_leaderboard)
                    }, 1000)
                }
            } else {
                playSound(R.raw.flip_back)
                cardGrid.postDelayed({
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
        soundPool.release()
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
        frontImgPath: String? = null,
        onFlipped: (() -> Unit)? = null
    ) {
        card.isEnabled = false

        card.animate()
            .rotationY(90f)
            .setDuration(150)
            .withEndAction {
                if (showFront && frontImgPath != null) {
                    lifecycleScope.launch {
                        val bitmap = withContext(Dispatchers.IO) {
                            BitmapFactory.decodeFile(frontImgPath)
                        }
                        card.setImageBitmap(bitmap ?: BitmapFactory.decodeResource(resources, R.drawable.card_loading))
                    }
                } else {
                    card.setImageResource(R.drawable.card_back)
                }
                card.rotationY = 90f
                card.animate()
                    .rotationY(180f)
                    .setDuration(150)
                    .withEndAction {
                        card.isEnabled = true
                        onFlipped?.invoke()
                    }
                    .start()
            }
            .start()
    }


    private fun playSoundEffect(flip:Boolean){
        val soundId = if (flip) R.raw.flip else R.raw.error
        soundMap[soundId]?.let {
            soundPool.play(it, 1f, 1f, 1, 0, 1f)
        }
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
        playSound(R.raw.match_music)
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

    private fun playSound(resId: Int) {
        soundMap[resId]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
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
