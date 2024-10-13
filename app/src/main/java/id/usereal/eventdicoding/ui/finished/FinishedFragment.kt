package id.usereal.eventdicoding.ui.finished

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.usereal.eventdicoding.databinding.FragmentFinishedBinding
import id.usereal.eventdicoding.viewmodel.FinishedViewModel

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FinishedAdapter

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
        setupViewModel()
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarFinished)
        (activity as AppCompatActivity).supportActionBar?.title = "Finished Event"
    }

    private fun setupRecyclerView() {
        adapter = FinishedAdapter()
        binding.tvEventFinished.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.tvEventFinished.adapter = adapter
    }

    private fun setupViewModel() {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}