package id.usereal.eventdicoding.ui.upcoming

import EventAdapter
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

class UpcomingFragment : Fragment() {

    private lateinit var binding: FragmentUpcomingBinding
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarUpcoming)
        (activity as AppCompatActivity).supportActionBar?.title = "Upcoming Event"
        adapter = EventAdapter()

       with(binding){
           tvEventUpcoming.adapter = adapter
           tvEventUpcoming.layoutManager = LinearLayoutManager(requireContext())
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
            tvEventUpcoming.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun showNoEventText(show: Boolean) {
        with(binding) {
            tvNoEventUpcoming.visibility = if (show) View.VISIBLE else View.GONE
            tvEventUpcoming.visibility = if (show) View.GONE else View.VISIBLE
        }
    }
}
