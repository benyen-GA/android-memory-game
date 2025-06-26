package iss.nus.edu.sg.memory_game.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.memory_game.R
import iss.nus.edu.sg.memory_game.adapter.ImageAdapter
import iss.nus.edu.sg.memory_game.model.ImageItem
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class FetchFragment : Fragment() {

    private val imageItems = mutableListOf<ImageItem>()
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var confirmButton: Button
    private var fetchJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_fetch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        confirmButton = view.findViewById(R.id.btnConfirm)
        val progressBar = view.findViewById<ProgressBar>(R.id.fetchProgressBar)
        val progressText = view.findViewById<TextView>(R.id.tvProgress)
        val btnFetch = view.findViewById<Button>(R.id.btnFetch)
        val etUrl = view.findViewById<EditText>(R.id.etUrl)

        confirmButton.isEnabled = false

        imageAdapter = ImageAdapter(imageItems) { selectedCount ->
            confirmButton.isEnabled = selectedCount == 6
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        recyclerView.adapter = imageAdapter

        btnFetch.setOnClickListener {
            fetchJob?.cancel()

            val urlInput = etUrl.text.toString().trim().let {
                if (it.startsWith("http://") || it.startsWith("https://")) it else "https://$it"
            }

            imageItems.clear()
            imageAdapter.notifyDataSetChanged()

            progressBar.progress = 0
            progressBar.visibility = View.VISIBLE
            progressText.visibility = View.VISIBLE
            progressText.text = "Starting download…"

            fetchJob = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val doc = Jsoup.connect(urlInput)
                        .userAgent("Mozilla")
                        .get()

                    val urls = doc.select("img[src]")
                        .map { it.absUrl("src") }
                        .filter { it.endsWith(".jpg") || it.endsWith(".jpeg") || it.endsWith(".png") }
                        .take(20)

                    withContext(Dispatchers.Main) {
                        progressBar.max = urls.size
                    }

                    urls.forEachIndexed { index, imageUrl ->
                        try {
                            val connection = URL(imageUrl).openConnection().apply {
                                setRequestProperty("User-Agent", "Mozilla")
                            }
                            connection.getInputStream().use { input ->
                                val bmp = BitmapFactory.decodeStream(input)
                                if (bmp != null) {
                                    withContext(Dispatchers.Main) {
                                        val config = bmp.config ?: Bitmap.Config.ARGB_8888
                                        imageItems.add(ImageItem(bmp.copy(config, true)))
                                        imageAdapter.notifyItemInserted(imageItems.size - 1)
                                        progressBar.progress = index + 1
                                        progressText.text = "Downloading ${index + 1} of ${urls.size} images…"
                                    }
                                }
                            }
                        } catch (_: Exception) {
                            // silently skip
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to fetch images.", Toast.LENGTH_LONG).show()
                    }
                } finally {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        progressText.visibility = View.GONE
                    }
                }
            }
        }

        confirmButton.setOnClickListener {
            val selected = imageItems.filter { it.isSelected }
            if (selected.size != 6) {
                Toast.makeText(requireContext(), "Please select exactly 6 images", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                selected.forEachIndexed { index, item ->
                    val file = File(requireContext().cacheDir, "image_${index + 1}.jpg")
                    FileOutputStream(file).use {
                        item.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
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
