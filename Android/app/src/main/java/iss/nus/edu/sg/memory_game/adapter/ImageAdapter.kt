package iss.nus.edu.sg.memory_game.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.memory_game.R
import iss.nus.edu.sg.memory_game.model.ImageItem

class ImageAdapter(
    val images: List<ImageItem>,
    private val onSelectionChanged: (Int) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val overlay: View = view.findViewById(R.id.overlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = images[position]
        holder.imageView.setImageBitmap(item.bitmap)
        holder.overlay.visibility = if (item.isSelected) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val selectedCount = images.count { it.isSelected }
            if (!item.isSelected && selectedCount < 6) {
                item.isSelected = true
            } else if (item.isSelected) {
                item.isSelected = false
            }
            notifyItemChanged(position)
            onSelectionChanged(images.count { it.isSelected })
        }
    }
}
