package com.dicoding.wanmuhtd.dicodingeventsapp.ui.favorite

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.wanmuhtd.dicodingeventsapp.data.Result
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.FragmentFavoriteEventBinding
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.ViewModelFactory
import com.dicoding.wanmuhtd.dicodingeventsapp.adapter.FavoriteEventAdapter
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.detail.DetailActivity
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.SettingPreferences
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.setting.dataStore


class FavoriteEventFragment : Fragment() {

    private var _binding: FragmentFavoriteEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pref = SettingPreferences.getInstance(requireActivity().dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireActivity(), pref)
        val viewModel: FavoriteEventViewModel by viewModels<FavoriteEventViewModel> { factory }
        val favoriteEventsAdapter = FavoriteEventAdapter { event ->
            Toast.makeText(requireContext(), "Clicked: ${event.name}", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
            intent.putExtra(DetailActivity.EXTRA_EVENT_STATUS, event.statusEvent)
            startActivity(intent)
        }

        val searchEventAdapter = FavoriteEventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
            intent.putExtra(DetailActivity.EXTRA_EVENT_STATUS, event.statusEvent)
            startActivity(intent)
        }

        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { event ->
            favoriteEventsAdapter.submitList(event)
        }

        with(binding) {
            svFavoriteEvents.setupWithSearchBar(sbFavoriteEvents)
            svFavoriteEvents.editText.setOnEditorActionListener { textView, _, _ ->
                val query = textView.text.toString()
                svFavoriteEvents.hide()

                viewModel.searchEvents(query).observe(viewLifecycleOwner) { filterResult ->
                    when (filterResult) {
                        is Result.Loading -> progressBar.visibility = View.VISIBLE
                        is Result.Success -> {
                            progressBar.visibility = View.GONE
                            if (filterResult.data.isEmpty()) {
                                tvNoResult.visibility = View.VISIBLE
                                rvFavoriteEventsSearch.visibility = View.GONE
                                rvFavoriteEvents.visibility = View.GONE
                            } else {
                                tvNoResult.visibility = View.GONE
                                searchEventAdapter.submitList(filterResult.data)
                                rvFavoriteEventsSearch.visibility = View.VISIBLE
                                rvFavoriteEvents.visibility = View.GONE
                            }
                            sbFavoriteEvents.setText(svFavoriteEvents.text)

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

        binding.rvFavoriteEvents.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = favoriteEventsAdapter
        }

        binding.rvFavoriteEventsSearch.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchEventAdapter
        }
        binding.rvFavoriteEvents.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = favoriteEventsAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}