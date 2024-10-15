package id.usereal.eventdicoding.ui.upcoming

import EventAdapter
import SearchAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.usereal.eventdicoding.data.Results
import id.usereal.eventdicoding.databinding.FragmentUpcomingBinding
import id.usereal.eventdicoding.viewmodel.EventViewModel
import kotlinx.coroutines.launch

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EventAdapter
    private lateinit var searchAdapter: SearchAdapter
    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private val TAG = "UpcomingFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        observeUpcomingEvents()
        setupSearchFunctionality()
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(binding.toolbarUpcoming)
            supportActionBar?.title = "Upcoming Event"
        }
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter()
        binding.rvEventUpcoming.apply {
            adapter = this@UpcomingFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        searchAdapter = SearchAdapter()
        binding.rvSearchEvent.apply {
            adapter = this@UpcomingFragment.searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearchFunctionality() {
        binding.searchInputUpcoming.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                performSearch(binding.searchInputUpcoming.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
        binding.textField.setEndIconOnClickListener { clearSearch() }
        binding.searchInputUpcoming.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    showInitialView() // Show upcoming events when input is cleared
                } else {
                    // Optionally, keep the upcoming events visible while typing
                    updateVisibilityForSearchResults(false) // Keep upcoming events visible
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun performSearch(query: String) {
        Log.d(TAG, "Performing search for query: $query")
        if (query.isNotEmpty()) {
            eventViewModel.searchEvent(query, 1).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Results.Loading -> showLoading(true)
                    is Results.Success -> {
                        showLoading(false)
                        val events = result.data
                        Log.d(TAG, "Search results loaded: ${events.size} items")
                        searchAdapter.submitList(events)
                        updateVisibilityForSearchResults(true) // Hide upcoming events
                    }
                    is Results.Error -> {
                        showLoading(false)
                        showSnackbar(result.error)
                        updateVisibilityForSearchResults(false) // No results found
                        Log.e(TAG, "Error loading search results: ${result.error}")
                    }
                }
            }
        } else {
            clearSearch() // Clear search if the query is empty
        }
    }

    private fun clearSearch() {
        Log.d(TAG, "Clearing search input")
        binding.searchInputUpcoming.text?.clear()
        updateVisibilityForSearchResults(false) // Show upcoming events again
        showInitialView()
    }

    private fun showInitialView() {
        binding.rvEventUpcoming.visibility = View.VISIBLE
        binding.rvSearchEvent.visibility = View.GONE
        binding.tvNoEventUpcoming.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun updateVisibilityForSearchResults(isSearching: Boolean) {
        binding.rvSearchEvent.visibility = if (isSearching) View.VISIBLE else View.GONE
        binding.rvEventUpcoming.visibility = if (isSearching) View.GONE else View.VISIBLE
        binding.tvNoEventUpcoming.visibility = if (isSearching && searchAdapter.itemCount == 0) View.VISIBLE else View.GONE
        Log.d(TAG, "Updating visibility for search results: isSearching=$isSearching, itemCount=${searchAdapter.itemCount}")
    }

    private fun showNoEventText(show: Boolean) {
        binding.tvNoEventUpcoming.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun observeUpcomingEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventViewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Results.Loading -> {
                            showLoading(true)
                        }
                        is Results.Success -> {
                            showLoading(false)
                            val events = result.data
                            Log.d(TAG, "Upcoming events loaded: ${events.size} items")
                            adapter.submitList(events)
                            showNoEventText(events.isEmpty())
                        }
                        is Results.Error -> {
                            showLoading(false)
                            showSnackbar(result.error)
                            showNoEventText(true) // Show "No Event" text on error as well
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    override fun onResume() {
        super.onResume()
        clearSearch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
