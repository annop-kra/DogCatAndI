package com.example.dogcatandi.presentation.cats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogcatandi.databinding.FragmentCatsBinding
import com.example.dogcatandi.presentation.cats.adapter.CatBreedsLoadStateAdapter
import com.example.dogcatandi.presentation.cats.adapter.CatBreedsPagingAdapter
import com.example.dogcatandi.presentation.utils.extensions.showRetryDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CatsFragment : Fragment() {

    private var _binding: FragmentCatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CatsViewModel by viewModel()
    private lateinit var adapter: CatBreedsPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeToRefresh()
        observeUiState()
    }

    private fun setupRecyclerView() {
        adapter = CatBreedsPagingAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CatsFragment.adapter.withLoadStateFooter(
                footer = CatBreedsLoadStateAdapter { this@CatsFragment.adapter.retry() }
            )
        }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadCatBreeds()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest(::render)
        }
    }

    private fun render(state: CatsUiState) {
        when (state) {
            is CatsUiState.Loading -> showLoading()
            is CatsUiState.Success -> showContent(state)
            is CatsUiState.Error -> showError(state.message)
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            swipeRefresh.isRefreshing = true
        }
    }

    private fun showContent(state: CatsUiState.Success) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            swipeRefresh.isRefreshing = false
        }
        lifecycleScope.launch {
            adapter.submitData(state.breeds)
        }
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            swipeRefresh.isRefreshing = false
        }
        requireContext().showRetryDialog(message) {
            viewModel.loadCatBreeds()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
