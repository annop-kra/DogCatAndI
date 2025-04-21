package com.example.dogcati.presentation.cats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dogcati.databinding.ItemLoadStateBinding

class CatBreedsLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<CatBreedsLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding, retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadStateViewHolder(
        private val binding: ItemLoadStateBinding,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(loadState: LoadState) {
            binding.progressBar.visibility = if (loadState is LoadState.Loading) View.VISIBLE else View.GONE
            binding.retryButton.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            binding.errorText.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE

            if (loadState is LoadState.Error) {
                binding.errorText.text = loadState.error.localizedMessage ?: "Unknown error"
            }

            binding.retryButton.setOnClickListener { retry() }
        }
    }
}
