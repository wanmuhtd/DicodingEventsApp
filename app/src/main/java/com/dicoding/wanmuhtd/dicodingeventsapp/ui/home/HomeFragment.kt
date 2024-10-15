package com.dicoding.wanmuhtd.dicodingeventsapp.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.dicoding.wanmuhtd.dicodingeventsapp.R
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.FragmentHomeBinding
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.DetailActivity
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.EventAdapter
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.HomeEventAdapter

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)

        val profileImageUrl = "https://media.licdn.com/dms/image/v2/D5603AQEsa_LlEj2LrQ/profile-displayphoto-shrink_800_800/profile-displayphoto-shrink_800_800/0/1718239463820?e=1734566400&v=beta&t=6KOteo786cVvtJzIwdCNvQpeM2skHO9XJpsizj5N6C0" //

        Glide.with(this)
            .load(profileImageUrl)
            .placeholder(R.drawable.baseline_account_circle_24)
            .error(R.drawable.baseline_account_circle_24)
            .circleCrop()
            .into(binding.ivProfile)

        binding.rvActiveEvents.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val activeEventAdapter = HomeEventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
            startActivity(intent)
        }
        binding.rvActiveEvents.adapter = activeEventAdapter

        binding.rvPastEvents.layoutManager = LinearLayoutManager(requireContext())
        val pastEventAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
            startActivity(intent)
        }
        binding.rvPastEvents.adapter = pastEventAdapter

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvActiveEvents)

        viewModel.activeEventList.observe(viewLifecycleOwner) { events ->
            val limitedEvents = if (events.size > 5) events.take(5) else events
            activeEventAdapter.submitList(limitedEvents)
        }

        viewModel.pastEventList.observe(viewLifecycleOwner) { events ->
            val limitedEvents = if (events.size > 5) events.take(5) else events
            pastEventAdapter.submitList(limitedEvents)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading, binding)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
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

    private fun showLoading(isLoading: Boolean, binding: FragmentHomeBinding) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}