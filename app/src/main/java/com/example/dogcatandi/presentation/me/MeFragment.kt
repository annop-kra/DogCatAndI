package com.example.dogcatandi.presentation.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.dogcatandi.R
import com.example.dogcatandi.databinding.FragmentMeBinding
import com.example.dogcatandi.domain.model.UserProfile
import com.example.dogcatandi.presentation.utils.extensions.showRetryDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MeFragment : Fragment() {

    private var _binding: FragmentMeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupReloadButton()
        observeUiState()
    }

    private fun setupReloadButton() {
        binding.btnReloadProfile.setOnClickListener {
            viewModel.loadUserProfile()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                render(state)
            }
        }
    }

    private fun render(state: MeUiState) {
        when (state) {
            is MeUiState.Loading -> showLoading()
            is MeUiState.Success -> showContent(state.userProfile)
            is MeUiState.Error -> showError(state.message)
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            profileLayout.visibility = View.GONE
            btnReloadProfile.isEnabled = false
        }
    }

    private fun showContent(user: UserProfile) {
        binding.apply {
            progressBar.visibility = View.GONE
            profileLayout.visibility = View.VISIBLE
            btnReloadProfile.isEnabled = true

            profileImage.load(user.profileImageUrl) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.image_placeholder)
                error(R.drawable.ic_profile_sample)
            }

            tvValueTitle.text = user.title
            tvValueFirstName.text = user.firstName
            tvValueLastName.text = user.lastName
            tvValueDateOfBirth.text = user.dateOfBirth
            tvValueAge.text = user.age.toString()

            val genderIcon = when (user.gender.lowercase()) {
                "male" -> R.drawable.ic_male
                "female" -> R.drawable.ic_female
                else -> R.drawable.ic_male
            }

            imgGender.load(genderIcon) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.image_placeholder)
                error(R.drawable.ic_profile_sample)
            }

            tvValueNationality.text = user.nationality
            tvValueMobile.text = user.phone
            tvValueAddress.text = user.address
        }
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            profileLayout.visibility = View.GONE
            btnReloadProfile.isEnabled = true
        }
        requireContext().showRetryDialog(message) {
            viewModel.loadUserProfile()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
