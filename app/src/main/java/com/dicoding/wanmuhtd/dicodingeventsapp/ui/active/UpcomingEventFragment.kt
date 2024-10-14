package com.dicoding.wanmuhtd.dicodingeventsapp.ui.active

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.FragmentUpcomingEventBinding
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.DetailActivity
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.EventAdapter

class UpcomingEventFragment : Fragment() {
    private val viewModel: UpcomingEventViewModel by viewModels()
    private var _binding: FragmentUpcomingEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentUpcomingEventBinding.bind(view)

        binding.rvActiveEvents.layoutManager = LinearLayoutManager(requireContext())
        val eventAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT, event)
            startActivity(intent)
        }
        binding.rvActiveEvents.adapter = eventAdapter

        viewModel.eventlist.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
        }

        binding.rvActiveEventsSearch.layoutManager = LinearLayoutManager(requireActivity())
        val searchEventAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT, event)
            startActivity(intent)
        }
        binding.rvActiveEventsSearch.adapter = searchEventAdapter

        viewModel.searchResults.observe(viewLifecycleOwner) { filteredEvents ->
            if(!filteredEvents.isNullOrEmpty()) {
                binding.rvActiveEventsSearch.visibility = View.VISIBLE
                binding.rvActiveEvents.visibility = View.GONE
                searchEventAdapter.submitList(filteredEvents)
            } else {
                binding.rvActiveEventsSearch.visibility = View.GONE
                binding.rvActiveEvents.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show()
            }
        }

        with(binding){
            svActiveEvents.setupWithSearchBar(sbActiveEvents)
            svActiveEvents
                .editText
                .setOnEditorActionListener { textView, _, _ ->
                    sbActiveEvents.setText(svActiveEvents.text)
                    svActiveEvents.hide()
                    viewModel.searchEvents(textView.text.toString())
                    false
                }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            binding.sbActiveEvents.clearText()

            if (binding.rvActiveEventsSearch.visibility == View.VISIBLE) {
                binding.rvActiveEventsSearch.visibility = View.GONE
                binding.rvActiveEvents.visibility = View.VISIBLE
            } else {
                findNavController().popBackStack()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
