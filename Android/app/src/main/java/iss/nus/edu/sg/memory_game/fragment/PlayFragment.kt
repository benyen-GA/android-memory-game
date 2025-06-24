package iss.nus.edu.sg.memory_game.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import iss.nus.edu.sg.memory_game.R

class PlayFragment : Fragment() {
    private lateinit var cardGrid: GridLayout
    private lateinit var imageList: List<Int>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_play, container, false)
        cardGrid = view.findViewById(R.id.cardGrid)

        showCardBacks()

        return view
    }

    private fun showCardBacks(){
        cardGrid.removeAllViews()

        val columns = 3

        val baseImg = listOf(
            R.drawable.card_1_test,
            R.drawable.card_2_test,
            R.drawable.card_3_test,
            R.drawable.card_4_test,
            R.drawable.card_5_test,
            R.drawable.card_6_test
        )

        val duplicatedImg = baseImg + baseImg

        imageList = duplicatedImg.shuffled()

        for (i in 0..11){
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
                    this.setImageResource(imageList[i])
                }
            }
            cardGrid.addView(card)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.btnToLeaderboard)?.setOnClickListener {
            view.findNavController().navigate(R.id.action_play_to_leaderboard)
        }

        childFragmentManager.beginTransaction().replace(R.id.adView, AdFragment()).commit()
    }
}
