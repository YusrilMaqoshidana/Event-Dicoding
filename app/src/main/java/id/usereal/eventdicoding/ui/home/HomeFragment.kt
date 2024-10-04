package id.usereal.eventdicoding.ui.home

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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.usereal.eventdicoding.databinding.FragmentHomeBinding
import id.usereal.eventdicoding.ui.finished.FinishedViewModel
import id.usereal.eventdicoding.ui.upcoming.UpcomingViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var upcomingAdapter: HomeAdapter
    private lateinit var finishedAdapter: EventAdapter
    private lateinit var searchAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.title = "Home"

        val upcomingViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[UpcomingViewModel::class.java]
        val finishedViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[FinishedViewModel::class.java]
        val homeViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[HomeViewModel::class.java]

        upcomingAdapter = HomeAdapter()
        finishedAdapter = EventAdapter()
        searchAdapter = EventAdapter()

        setupRecyclerViews()
        setupSearchFunctionality(homeViewModel)

        upcomingViewModel.fetchApiUpcoming()
        finishedViewModel.fetchApiFinished()

        observeViewModels(upcomingViewModel, finishedViewModel, homeViewModel)
    }

    private fun setupRecyclerViews() {

        binding.rvUpcomingEvents.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

        binding.rvFinishedEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = finishedAdapter
        }

        binding.rvSearchEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchAdapter
            visibility = View.GONE
        }
    }

    private fun observeViewModels(upcomingViewModel: UpcomingViewModel, finishedViewModel: FinishedViewModel, homeViewModel: HomeViewModel) {

        upcomingViewModel.eventsUpcoming.observe(viewLifecycleOwner) { eventList ->
            upcomingAdapter.submitList(if (eventList.size < 5) eventList else eventList.take(5))
        }

        finishedViewModel.eventsFinished.observe(viewLifecycleOwner) { eventList ->
            finishedAdapter.submitList(if (eventList.size < 5) eventList else eventList.take(5))
        }

        homeViewModel.eventSearch.observe(viewLifecycleOwner) { eventList ->
            searchAdapter.submitList(eventList)
            updateVisibilityForSearchResults(eventList.isNotEmpty())
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        upcomingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        upcomingViewModel.showNoEvent.observe(viewLifecycleOwner) { show ->
            showNoEventTextUpcoming(show)
        }

        finishedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        finishedViewModel.showNoEvent.observe(viewLifecycleOwner) { show ->
            showNoEventTextFinished(show)
        }
        finishedViewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
        homeViewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
        upcomingViewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun setupSearchFunctionality(homeViewModel: HomeViewModel) {
        binding.searchInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                performSearch(binding.searchInput.text.toString(), homeViewModel)
                return@setOnEditorActionListener true
            }
            false
        }

        binding.textField.setEndIconOnClickListener { clearSearch() }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    clearSearch()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun performSearch(query: String, homeViewModel: HomeViewModel) {
        if (query.isNotEmpty()) {
            homeViewModel.searchEvent(query)
        }
    }

    private fun clearSearch() {
        binding.searchInput.text?.clear()
        updateVisibilityForSearchResults(false)
        showInitialView()
    }

    private fun showInitialView() {
        binding.rvUpcomingEvents.visibility = View.VISIBLE
        binding.rvFinishedEvents.visibility = View.VISIBLE
        binding.tvUpcomingEvents.visibility = View.VISIBLE
        binding.tvFinishedEvents.visibility = View.VISIBLE
        binding.rvSearchEvent.visibility = View.GONE
    }

    private fun updateVisibilityForSearchResults(hasResults: Boolean) {
        binding.rvUpcomingEvents.visibility = View.GONE
        binding.rvFinishedEvents.visibility = View.GONE
        binding.tvUpcomingEvents.visibility = View.GONE
        binding.tvFinishedEvents.visibility = View.GONE
        binding.noEventFinished.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        binding.rvSearchEvent.visibility = if (hasResults) View.VISIBLE else View.GONE
        binding.noEventUpcoming.visibility = if (hasResults) View.GONE else View.VISIBLE
    }

    private fun showNoEventTextUpcoming(show: Boolean) {
        binding.noEventUpcoming.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvUpcomingEvents.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvFinishedEvents.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.rvUpcomingEvents.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.tvFinishedEvents.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.tvUpcomingEvents.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showNoEventTextFinished(show: Boolean) {
        binding.noEventFinished.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvFinishedEvents.visibility = if (show) View.GONE else View.VISIBLE
    }
}
