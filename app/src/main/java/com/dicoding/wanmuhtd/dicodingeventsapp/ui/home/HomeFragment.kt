package com.dicoding.wanmuhtd.dicodingeventsapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.dicoding.wanmuhtd.dicodingeventsapp.R
import com.dicoding.wanmuhtd.dicodingeventsapp.adapter.HomeEventAdapter
import com.dicoding.wanmuhtd.dicodingeventsapp.adapter.PastEventAdapter
import com.dicoding.wanmuhtd.dicodingeventsapp.data.Result
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.FragmentHomeBinding
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.ViewModelFactory
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.detail.DetailActivity
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.SettingPreferences
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.dataStore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = SettingPreferences.getInstance(requireActivity().dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext(), pref)
        val viewModel: HomeViewModel by viewModels<HomeViewModel> { factory }
        val pastEventsAdapter = PastEventAdapter { event ->
            Toast.makeText(requireContext(), "Clicked: ${event.name}", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
            intent.putExtra(DetailActivity.EXTRA_EVENT_STATUS, false)
            startActivity(intent)
        }
        val upcomingEventAdapter = HomeEventAdapter { event ->
            Toast.makeText(requireContext(), "Clicked: ${event.name}", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
            intent.putExtra(DetailActivity.EXTRA_EVENT_STATUS, true)
            startActivity(intent)
        }

        val profileImageUrl =
            "https://media.licdn.com/dms/image/v2/D5603AQEsa_LlEj2LrQ/profile-displayphoto-shrink_800_800/profile-displayphoto-shrink_800_800/0/1718239463820?e=1734566400&v=beta&t=6KOteo786cVvtJzIwdCNvQpeM2skHO9XJpsizj5N6C0" //

        Glide.with(this)
            .load(profileImageUrl)
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .circleCrop()
            .into(binding.ivProfile)

        viewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.progressBarActive.visibility = View.VISIBLE
                is Result.Success -> {
                    binding.progressBarActive.visibility = View.GONE
                    val limitedList = result.data.take(5)
                    upcomingEventAdapter.submitList(limitedList)
                }

                is Result.Error -> {
                    binding.progressBarActive.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Terjadi kesalahan: ${result.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        viewModel.getPastEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.progressBarPast.visibility = View.VISIBLE
                is Result.Success -> {
                    binding.progressBarPast.visibility = View.GONE
                    val limitedList = result.data.take(5)
                    pastEventsAdapter.submitList(limitedList)
                }

                is Result.Error -> {
                    binding.progressBarPast.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Terjadi kesalahan: ${result.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.rvActiveEvents.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingEventAdapter
            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(this)
        }

        binding.rvPastEvents.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pastEventsAdapter
        }

        binding.btnShowActiveEvents.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_active_events,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.navigation_home, inclusive = false)
                    .build()
            )
        }

        binding.btnShowPastEvents.setOnClickListener {
            findNavController().navigate(
                R.id.action_navigation_home_to_navigation_past_events,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.navigation_home, inclusive = false)
                    .build()
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}