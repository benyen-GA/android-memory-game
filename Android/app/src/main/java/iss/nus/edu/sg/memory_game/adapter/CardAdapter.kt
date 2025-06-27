package iss.nus.edu.sg.memory_game.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.memory_game.R

class CardAdapter(
    private val imagePaths: List<String>,
    private val bitmapCache: Map<String, Bitmap>,
    private val onCardClick: (position: Int, imagePath: String, cardView: ImageView) -> Unit
) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    private val matchedPositions = mutableSetOf<Int>()
    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.cardImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        if (matchedPositions.contains(position)) {
            holder.imageView.setImageBitmap(bitmapCache[imagePaths[position]])
            holder.imageView.tag = "matched"
            holder.imageView.isEnabled = false
        } else {
            holder.imageView.setImageResource(R.drawable.card_back)
            holder.imageView.tag = "back"
            holder.imageView.isEnabled = true
        }

        holder.imageView.setOnClickListener {
            if (holder.imageView.tag == "matched" || !holder.imageView.isEnabled || !holder.imageView.isClickable) {
                return@setOnClickListener
            }
            animateFlip(holder.imageView) {
                onCardClick(position, imagePaths[position], holder.imageView)
            }
        }
    }

    fun markAsMatched(imageView: ImageView, position: Int) {
        matchedPositions.add(position)
        imageView.tag = "matched"
        imageView.isEnabled = false
    }

    override fun getItemCount(): Int = imagePaths.size

    fun revealImage(position: Int, imageView: ImageView) {
        val path = imagePaths[position]
        bitmapCache[path]?.let {
            animateFlip(imageView) {
                imageView.setImageBitmap(it)
                imageView.tag = "front"
            }
        }
    }

    fun hideImage(imageView: ImageView) {
        animateFlip(imageView) {
            imageView.setImageResource(R.drawable.card_back)
            imageView.tag = "back"
            imageView.isEnabled = true
        }
    }

    private fun animateFlip(imageView: ImageView, onMidFlip: () -> Unit) {
        imageView.animate()
            .rotationY(90f)
            .setDuration(150)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                onMidFlip()
                imageView.rotationY = -90f
                imageView.animate()
                    .rotationY(0f)
                    .setDuration(150)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
            .start()
    }
}
