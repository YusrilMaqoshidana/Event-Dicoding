package id.usereal.eventdicoding.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.usereal.eventdicoding.databinding.FragmentUpcomingBinding
import id.usereal.eventdicoding.viewmodel.UpcomingViewModel

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: UpcomingAdapter

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

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarUpcoming)
        (activity as AppCompatActivity).supportActionBar?.title = "Upcoming Event"
        adapter = UpcomingAdapter()

        with(binding) {
            rvEventUpcoming.adapter = adapter
            rvEventUpcoming.layoutManager = LinearLayoutManager(requireContext())
        }

        val upcomingViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[UpcomingViewModel::class.java]
        upcomingViewModel.fetchApiUpcoming()
        upcomingViewModel.eventsUpcoming.observe(viewLifecycleOwner) { eventList ->
            adapter.submitList(eventList)
        }
        upcomingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        upcomingViewModel.showNoEvent.observe(viewLifecycleOwner) { show ->
            showNoEventText(show)
        }

        upcomingViewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            rvEventUpcoming.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun showNoEventText(show: Boolean) {
        with(binding) {
            tvNoEventUpcoming.visibility = if (show) View.VISIBLE else View.GONE
            rvEventUpcoming.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}