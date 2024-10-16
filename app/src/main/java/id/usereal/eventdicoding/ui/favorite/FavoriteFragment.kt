package id.usereal.eventdicoding.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.usereal.eventdicoding.adapter.FavoriteAdapter
import id.usereal.eventdicoding.data.Results
import id.usereal.eventdicoding.databinding.FragmentFavoriteBinding
import id.usereal.eventdicoding.viewmodel.FavoriteViewModel
import id.usereal.eventdicoding.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FavoriteAdapter
    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavoriteEvents()
    }

    private fun setupRecyclerView() {
        adapter = FavoriteAdapter()
        binding.tvEventFavorite.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@FavoriteFragment.adapter
        }
    }

    private fun showNoEventText(show: Boolean) {
        binding.tvNoEventFavorite.visibility = if (show) View.VISIBLE else View.GONE
        binding.tvEventFavorite.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun observeFavoriteEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteEvents.collect { result ->
                    when (result) {
                        is Results.Loading -> showLoading(true)
                        is Results.Success -> {
                            showLoading(false)
                            val events = result.data
                            adapter.submitList(events)
                            showNoEventText(events.isEmpty())
                        }
                        is Results.Error -> {
                            showLoading(false)
                            showSnackbar(result.error)
                            showNoEventText(true)  // Show "No Event" text on error as well
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvEventFavorite.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}