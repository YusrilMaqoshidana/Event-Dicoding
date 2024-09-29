package id.usereal.eventdicoding.ui.finished

import EventAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.usereal.eventdicoding.data.Event
import id.usereal.eventdicoding.databinding.FragmentFinishedBinding

class FinishedFragment : Fragment() {

    private lateinit var binding: FragmentFinishedBinding

    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFinishedBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val finishedViewModel =
            ViewModelProvider(this).get(FinishedViewModel::class.java)
        finishedViewModel.fetchFinishedEvents()

        finishedViewModel.events.observe(viewLifecycleOwner){ event ->
            setupRecyclerView(event)
        }
        binding.tvEventFinished.layoutManager = LinearLayoutManager(requireContext())
        
        finishedViewModel.isLoading.observe(viewLifecycleOwner){
            showLoading(it)
        }

        finishedViewModel.showNoEvent.observe(viewLifecycleOwner){
            showNoEventText(it)
        }

    }

    private fun setupRecyclerView(listEvent: List<Event>) {
        binding.tvEventFinished.layoutManager = LinearLayoutManager(requireContext())
        adapter = EventAdapter(listEvent)
        binding.tvEventFinished.adapter = adapter
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvEventFinished.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showNoEventText(show: Boolean) {
        binding.tvNoEventFinished.visibility = if (show) View.VISIBLE else View.GONE
        binding.tvEventFinished.visibility = if (show) View.GONE else View.VISIBLE
    }
}