package com.dicoding.wanmuhtd.dicodingeventsapp.ui.past

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
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.FragmentPastEventBinding
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.DetailActivity
import com.dicoding.wanmuhtd.dicodingeventsapp.ui.EventAdapter

class PastEventFragment : Fragment() {
    private val viewModel: PastEventViewModel by viewModels()
    private var _binding: FragmentPastEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPastEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPastEventBinding.bind(view)

        binding.rvPastEvents.layoutManager = LinearLayoutManager(requireContext())
        val eventAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
            startActivity(intent)
        }
        binding.rvPastEvents.adapter = eventAdapter

        viewModel.eventlist.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
        }

        binding.rvPastEventsSearch.layoutManager = LinearLayoutManager(requireActivity())
        val searchEventAdapter = EventAdapter { event ->
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_EVENT_ID, event.id)
            startActivity(intent)
        }

        binding.rvPastEventsSearch.adapter = searchEventAdapter

        viewModel.searchResults.observe(viewLifecycleOwner) { filteredEvents ->
            if(!filteredEvents.isNullOrEmpty()) {
                binding.rvPastEventsSearch.visibility = View.VISIBLE
                binding.rvPastEvents.visibility = View.GONE
                searchEventAdapter.submitList(filteredEvents)
            } else {
                binding.rvPastEventsSearch.visibility = View.GONE
                binding.rvPastEvents.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show()
            }
        }

        with(binding){
            svPastEvents.setupWithSearchBar(sbPastEvents)
            svPastEvents
                .editText
                .setOnEditorActionListener { textView, _, _ ->
                    sbPastEvents.setText(svPastEvents.text)
                    svPastEvents.hide()
                    viewModel.searchEvents(textView.text.toString())
                    false
                }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            binding.sbPastEvents.clearText()

            if (binding.rvPastEventsSearch.visibility == View.VISIBLE) {
                binding.rvPastEventsSearch.visibility = View.GONE
                binding.rvPastEvents.visibility = View.VISIBLE
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
