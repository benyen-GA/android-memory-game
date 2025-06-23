package iss.nus.edu.sg.memory_game.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.memory_game.R
import iss.nus.edu.sg.memory_game.adapter.ImageAdapter
import iss.nus.edu.sg.memory_game.model.ImageItem
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap

class FetchFragment : Fragment() {

    private val imageItems = mutableListOf<ImageItem>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var confirmButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_fetch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        confirmButton = view.findViewById(R.id.btnConfirm)
        val progressBar = view.findViewById<ProgressBar>(R.id.fetchProgressBar)
        val btnFetch = view.findViewById<Button>(R.id.btnFetch)

        confirmButton.isEnabled = false

        imageAdapter = ImageAdapter(imageItems) { selectedCount ->
            confirmButton.isEnabled = selectedCount == 6
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerView.adapter = imageAdapter

        btnFetch.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            imageItems.clear()
            val bmp = BitmapFactory.decodeResource(requireContext().resources, R.drawable.placeholder_image)
            repeat(20) {
                val safeConfig = bmp.config ?: Bitmap.Config.ARGB_8888
                imageItems.add(ImageItem(bmp.copy(safeConfig, true)))
            }
            imageAdapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        }

        confirmButton.setOnClickListener {
            val selected = imageItems.filter { it.isSelected }
            if (selected.size != 6) {
                Toast.makeText(requireContext(), "Please select exactly 6 images", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                selected.forEachIndexed { index, imageItem ->
                    val file = File(requireContext().cacheDir, "image_${index + 1}.jpg")
                    FileOutputStream(file).use {
                        imageItem.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    }
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Images saved to cache", Toast.LENGTH_SHORT).show()
                    view.findNavController().navigate(R.id.action_fetch_to_play)
                }
            }
        }
    }
}
