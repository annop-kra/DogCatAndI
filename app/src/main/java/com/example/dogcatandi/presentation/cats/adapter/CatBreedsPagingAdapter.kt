package com.example.dogcatandi.presentation.cats.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dogcatandi.R
import com.example.dogcatandi.databinding.ItemCatBreedBinding
import com.example.dogcatandi.domain.model.CatBreed

class CatBreedsPagingAdapter : PagingDataAdapter<CatBreed, CatBreedsPagingAdapter.CatBreedViewHolder>(CAT_BREED_COMPARATOR) {

    private var expandedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatBreedViewHolder {
        val binding = ItemCatBreedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatBreedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatBreedViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it, position) }
    }

    inner class CatBreedViewHolder(
        private val binding: ItemCatBreedBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(catBreed: CatBreed, position: Int) {
            val isExpanded = position == expandedPosition

            binding.tvBreed.text = catBreed.breed
            binding.detailsContainer.visibility = if (isExpanded) View.VISIBLE else View.GONE
            binding.countryText.text = binding.root.context.getString(R.string.label_cat_country, catBreed.country)
            binding.originText.text = binding.root.context.getString(R.string.label_cat_origin, catBreed.origin)
            binding.coatText.text = binding.root.context.getString(R.string.label_cat_coat, catBreed.coat)
            binding.patternText.text = binding.root.context.getString(R.string.label_cat_pattern, catBreed.pattern)

            if (isExpanded) {
                binding.arrowIcon.setImageResource(R.drawable.ic_arrow_top)
            } else {
                binding.arrowIcon.setImageResource(R.drawable.ic_arrow_down)
            }

            binding.root.setOnClickListener {
                val previousExpanded = expandedPosition
                expandedPosition = if (isExpanded) -1 else position

                if (previousExpanded != -1) notifyItemChanged(previousExpanded)
                if (expandedPosition != -1) notifyItemChanged(expandedPosition)
            }
        }
    }

    companion object {
        private val CAT_BREED_COMPARATOR = object : DiffUtil.ItemCallback<CatBreed>() {
            override fun areItemsTheSame(oldItem: CatBreed, newItem: CatBreed): Boolean {
                return oldItem.breed == newItem.breed
            }

            override fun areContentsTheSame(oldItem: CatBreed, newItem: CatBreed): Boolean {
                return oldItem == newItem
            }
        }
    }
}
