package id.usereal.eventdicoding.ui.upcoming
import EventAdapter
import UpcomingViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.usereal.eventdicoding.data.Event
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
        val upcomingViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[UpcomingViewModel::class.java]
        upcomingViewModel.fetchUpcomingEvents()
        upcomingViewModel.events.observe(viewLifecycleOwner) { event ->
            setupRecyclerView(event)
        }
        binding.tvEventUpcoming.layoutManager = LinearLayoutManager(requireContext())
        upcomingViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }
        upcomingViewModel.showNoEvent.observe(viewLifecycleOwner) {
            showNoEventText(it)
        }
    }

    private fun setupRecyclerView(listEvent: List<Event>) {
        binding.tvEventUpcoming.layoutManager = LinearLayoutManager(requireContext())
        adapter = EventAdapter(listEvent)
        binding.tvEventUpcoming.adapter = adapter
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvEventUpcoming.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showNoEventText(show: Boolean) {
        binding.tvNoEventUpcoming.visibility = if (show) View.VISIBLE else View.GONE
        binding.tvEventUpcoming.visibility = if (show) View.GONE else View.VISIBLE
    }

}
