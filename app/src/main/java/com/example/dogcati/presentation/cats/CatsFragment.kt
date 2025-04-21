package com.example.dogcati.presentation.cats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dogcati.databinding.FragmentCatsBinding
import com.example.dogcati.presentation.cats.adapter.CatBreedsLoadStateAdapter
import com.example.dogcati.presentation.cats.adapter.CatBreedsPagingAdapter
import com.example.dogcati.presentation.utils.extensions.showShortToast
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
        setupAdapter()
        setupObservers()
        setupRefreshListener()
    }

    private fun setupAdapter() {
        adapter = CatBreedsPagingAdapter()
        with(binding) {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@CatsFragment.adapter.withLoadStateFooter(
                    footer = CatBreedsLoadStateAdapter { this@CatsFragment.adapter.retry() }
                )
            }
        }
    }

    private fun setupObservers() {
        observeCatBreeds()
        observeLoadStates()
    }

    private fun observeCatBreeds() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.catBreeds.collectLatest { pagingData ->
                Log.d("CatsFragment", "Submitting new PagingData")
                adapter.submitData(pagingData)
            }
        }
    }

    private fun observeLoadStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                handleLoadingState(loadStates.refresh)
                handleErrorState(loadStates.refresh)
            }
        }
    }

    private fun handleLoadingState(state: LoadState) {
        with(binding) {
            progressBar.visibility = if (state is LoadState.Loading) View.VISIBLE else View.GONE
            recyclerView.visibility = if (state is LoadState.Loading) View.GONE else View.VISIBLE
            swipeRefresh.isRefreshing = state is LoadState.Loading
        }
    }

    private fun handleErrorState(state: LoadState) {
        if (state is LoadState.Error) {
            showShortToast("Error: ${state.error.message}")
        }
    }

    private fun setupRefreshListener() {
        with(binding) {
            swipeRefresh.setOnRefreshListener {
                Log.d("CatsFragment", "Refreshing data")
                adapter.refresh()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}