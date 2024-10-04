package id.usereal.eventdicoding.ui.finished

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarFinished)
        (activity as AppCompatActivity).supportActionBar?.title = "Finished Event"
        adapter = EventAdapter()
        binding.tvEventFinished.layoutManager = LinearLayoutManager(requireContext())
        binding.tvEventFinished.adapter = adapter

        val finishedViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[FinishedViewModel::class.java]

        finishedViewModel.fetchApiFinished()
        finishedViewModel.eventsFinished.observe(viewLifecycleOwner) { eventList ->
            adapter.submitList(eventList)
        }

        finishedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        finishedViewModel.showNoEvent.observe(viewLifecycleOwner) { show ->
            showNoEventText(show)
        }

        finishedViewModel.snackbarMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
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
