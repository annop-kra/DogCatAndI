package com.example.dogcatandi.presentation.dogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.dogcatandi.R
import com.example.dogcatandi.databinding.FragmentDogsBinding
import com.example.dogcatandi.presentation.utils.extensions.showRetryDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

class DogsFragment : Fragment() {

    private var _binding: FragmentDogsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DogsViewModel by viewModel()

    private val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
        observeUiState()
    }

    private fun setupButtons() {
        binding.btnConcurrentReload.setOnClickListener {
            viewModel.loadDogImagesConcurrently()
        }
        binding.btnSequentialReload.setOnClickListener {
            viewModel.loadDogImagesSequentially()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                render(state)
            }
        }
    }

    private fun render(state: DogsUiState) {
        with(binding) {
            when (state) {
                is DogsUiState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    setDogViewsVisible(false)
                    btnConcurrentReload.isEnabled = false
                    btnSequentialReload.isEnabled = false
                }
                is DogsUiState.Success -> {
                    progressBar.visibility = View.GONE
                    btnConcurrentReload.isEnabled = true
                    btnSequentialReload.isEnabled = true

                    renderDogImage(0, state.images.getOrNull(0), imageDogFirst, textLabelDogFirst, textTimestampDogFirst)
                    renderDogImage(1, state.images.getOrNull(1), imageDogSecond, textLabelDogSecond, textTimestampDogSecond)
                    renderDogImage(2, state.images.getOrNull(2), imageDogThird, textLabelDogThird, textTimestampDogThird)
                }
                is DogsUiState.Error -> {
                    progressBar.visibility = View.GONE
                    setDogViewsVisible(false)
                    btnConcurrentReload.isEnabled = true
                    btnSequentialReload.isEnabled = true
                    showErrorDialog(state.message)
                }
            }
        }
    }

    private fun setDogViewsVisible(visible: Boolean) = with(binding) {
        val visibility = if (visible) View.VISIBLE else View.GONE
        imageDogFirst.visibility = visibility
        imageDogSecond.visibility = visibility
        imageDogThird.visibility = visibility
        textLabelDogFirst.visibility = visibility
        textLabelDogSecond.visibility = visibility
        textLabelDogThird.visibility = visibility
        textTimestampDogFirst.visibility = visibility
        textTimestampDogSecond.visibility = visibility
        textTimestampDogThird.visibility = visibility
    }

    private fun renderDogImage(index: Int, image: DogImage?, imageView: ImageView, labelView: TextView, timestampView: TextView) {
        if (image != null) {
            imageView.visibility = View.VISIBLE
            labelView.visibility = View.VISIBLE
            timestampView.visibility = View.VISIBLE

            imageView.load(image.url) {
                placeholder(R.drawable.image_placeholder)
                error(R.drawable.image_placeholder)
            }
            timestampView.text = image.timestamp.format(formatter)
        } else {
            imageView.visibility = View.GONE
            labelView.visibility = View.GONE
            timestampView.visibility = View.GONE
        }
    }

    private fun showErrorDialog(message: String) {
        requireContext().showRetryDialog(message) {
            viewModel.loadDogImagesConcurrently()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
