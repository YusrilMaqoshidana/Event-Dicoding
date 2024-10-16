package id.usereal.eventdicoding.ui.finished

import EventAdapter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.usereal.eventdicoding.adapter.SearchAdapter
import id.usereal.eventdicoding.data.Results
import id.usereal.eventdicoding.databinding.FragmentFinishedBinding
import id.usereal.eventdicoding.viewmodel.EventViewModel
import id.usereal.eventdicoding.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EventAdapter
    private lateinit var searchAdapter: SearchAdapter
    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        observeFinishedEvents()
        setupSearchFunctionality()
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(binding.toolbarFinished)
            supportActionBar?.title = "Finished Event"
        }
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter()
        binding.tvEventFinished.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@FinishedFragment.adapter
        }

        searchAdapter = SearchAdapter()
        binding.rvSearchEvent.apply {
            adapter = this@FinishedFragment.searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearchFunctionality() {
        binding.searchInputFinished.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                performSearch(binding.searchInputFinished.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
        binding.textField.setEndIconOnClickListener { clearSearch() }
        binding.searchInputFinished.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    clearSearch()
                } else {
                    updateVisibilityForSearchResults(true)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            eventViewModel.searchEvent(query, 0).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Results.Loading -> showLoading(true)
                    is Results.Success -> {
                        showLoading(false)
                        val events = result.data
                        searchAdapter.submitList(events)
                        updateVisibilityForSearchResults(events.isNotEmpty())
                    }
                    is Results.Error -> {
                        showLoading(false)
                        showSnackbar(result.error)
                        updateVisibilityForSearchResults(false) // No results found
                    }
                }
            }
        }
    }

    private fun clearSearch() {
        binding.searchInputFinished.text?.clear()
        updateVisibilityForSearchResults(false)
        showInitialView()
    }

    private fun showInitialView() {
        binding.tvEventFinished.visibility = View.VISIBLE
        binding.rvSearchEvent.visibility = View.GONE
        binding.tvNoEventFinished.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun updateVisibilityForSearchResults(isSearching: Boolean) {
        binding.rvSearchEvent.visibility = if (isSearching) View.VISIBLE else View.GONE
        binding.tvEventFinished.visibility = if (isSearching) View.GONE else View.VISIBLE
        binding.tvNoEventFinished.visibility = if (isSearching && searchAdapter.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun showNoEventText(show: Boolean) {
        binding.tvNoEventFinished.visibility = if (show) View.VISIBLE else View.GONE
        binding.tvEventFinished.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun observeFinishedEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventViewModel.getFinishedEvents().observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Results.Loading -> {
                            showLoading(true)
                        }
                        is Results.Success -> {
                            showLoading(false)
                            val events = result.data
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
        binding.tvEventFinished.visibility = if (isLoading) View.GONE else View.VISIBLE
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
