package com.example.dogcati.presentation.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.example.dogcati.R
import com.example.dogcati.databinding.FragmentMeBinding
import com.example.dogcati.domain.model.UserProfile
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
        setupObservers()
        setupRefreshListener()
        viewModel.loadUserProfile()
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            with(binding) {
                progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                profileLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
            }
        }

        viewModel.userProfile.observe(viewLifecycleOwner) { user ->
            user?.let { bindUserData(it) }
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun bindUserData(user: UserProfile) {
        with(binding) {
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

    private fun setupRefreshListener() {
        with(binding) {
            swipeRefresh.setOnRefreshListener {
                viewModel.loadUserProfile(forceRefresh = true)
                swipeRefresh.isRefreshing = false
            }
            btnReload.setOnClickListener {
                viewModel.loadUserProfile(forceRefresh = true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
