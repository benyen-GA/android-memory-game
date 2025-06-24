package iss.nus.edu.sg.memory_game.model

import android.graphics.Bitmap

data class ImageItem(
    val bitmap: Bitmap,
    var isSelected: Boolean = false
)