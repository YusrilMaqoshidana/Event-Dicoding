package id.usereal.eventdicoding.ui.upcoming

import EventAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.usereal.eventdicoding.data.Results
import id.usereal.eventdicoding.databinding.FragmentUpcomingBinding
import id.usereal.eventdicoding.viewmodel.EventViewModel

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EventAdapter
    private val eventViewModel: EventViewModel by viewModels { ViewModelFactory.getInstance(requireContext()) }

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
        setupViewModel()
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
    }

    private fun setupViewModel() {
        eventViewModel.getUpcomingEvents().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Results.Loading -> showLoading(true)
                is Results.Success -> {
                    showLoading(false)
                    showNoEventText(result.data.isEmpty())
                    adapter.submitList(result.data)
                }
                is Results.Error -> {
                    showLoading(false)
                    showNoEventText(true)
                    showSnackbar(result.error)
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.let {
            it.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            it.rvEventUpcoming.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }
    private fun showNoEventText(show: Boolean) {
        binding.tvNoEventUpcoming.visibility = if (show) View.VISIBLE else View.GONE
        binding.rvEventUpcoming.visibility = if (show) View.GONE else View.VISIBLE
    }
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}