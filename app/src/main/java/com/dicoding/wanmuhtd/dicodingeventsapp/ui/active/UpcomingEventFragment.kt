package com.dicoding.wanmuhtd.dicodingeventsapp.ui.active

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.FragmentUpcomingEventBinding
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.ViewModelFactory
import com.dicoding.wanmuhtd.dicodingeventsapp.adapter.UpcomingEventAdapter
import com.dicoding.wanmuhtd.dicodingeventsapp.data.Result
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.detail.DetailActivity
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.SettingPreferences
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.dataStore

class UpcomingEventFragment : Fragment() {

    private var _binding: FragmentUpcomingEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext(), pref)
        val viewModel: UpcomingEventViewModel by viewModels<UpcomingEventViewModel> { factory }
        val upcomingEventsAdapter = UpcomingEventAdapter { event ->
            Toast.makeText(requireContext(), "Clicked: ${event.name}", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
            intent.putExtra(DetailActivity.EXTRA_EVENT_STATUS, true)
            startActivity(intent)
        }

        val searchEventAdapter = UpcomingEventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
            intent.putExtra(DetailActivity.EXTRA_EVENT_STATUS, true)
            startActivity(intent)
        }

        viewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    upcomingEventsAdapter.submitList(result.data)
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Terjadi kesalahan: ${result.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        with(binding) {
            svActiveEvents.setupWithSearchBar(sbActiveEvents)
            svActiveEvents.editText.setOnEditorActionListener { textView, _, _ ->
                val query = textView.text.toString()
                svActiveEvents.hide()

                viewModel.searchEvents(query).observe(viewLifecycleOwner) {  filterResult ->
                    when (filterResult) {
                        is Result.Loading -> progressBar.visibility = View.VISIBLE
                        is Result.Success -> {
                            progressBar.visibility = View.GONE
                            if (filterResult.data.isEmpty()) {
                                tvNoResult.visibility = View.VISIBLE
                                rvActiveEventsSearch.visibility = View.GONE
                                rvActiveEvents.visibility = View.GONE
                            } else {
                                tvNoResult.visibility = View.GONE
                                searchEventAdapter.submitList(filterResult.data)
                                rvActiveEventsSearch.visibility = View.VISIBLE
                                rvActiveEvents.visibility = View.GONE
                            }
                            sbActiveEvents.setText(svActiveEvents.text)
                        }

                        is Result.Error -> {
                            progressBar.visibility = View.GONE
                            Toast.makeText(
                                requireContext(),
                                "Terjadi kesalahan: ${filterResult.error}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                false
            }
        }

        binding.rvActiveEvents.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = upcomingEventsAdapter
        }

        binding.rvActiveEventsSearch.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchEventAdapter
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}