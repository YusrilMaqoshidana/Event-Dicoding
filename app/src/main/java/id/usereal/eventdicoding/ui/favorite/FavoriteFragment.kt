package id.usereal.eventdicoding.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import id.usereal.eventdicoding.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    // private lateinit var adapter: FavoriteAdapter
    // private lateinit var viewModel: FavoriteViewModel

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

        setupToolbar()
        setupRecyclerView()
        setupViewModel()
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarFavorite)
        (activity as AppCompatActivity).supportActionBar?.title = "Favorite Events"
    }

    private fun setupRecyclerView() {
        // adapter = FavoriteAdapter()
        //binding.rvEventFavorite.layoutManager = LinearLayoutManager(requireContext())
        // binding.rvEventFavorite.adapter = adapter
    }

    private fun setupViewModel() {
        // viewModel = ViewModelProvider(this)[FavoriteViewModel::class.java]

        // Observe LiveData here
        // Example:
        // viewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
        //     adapter.submitList(events)
        // }

        // viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
        //     showLoading(isLoading)
        // }

        // viewModel.showNoEvent.observe(viewLifecycleOwner) { show ->
        //     showNoEventText(show)
        // }
    }

//    private fun showLoading(isLoading: Boolean) {
//        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//        binding.rvEventFavorite.visibility = if (isLoading) View.GONE else View.VISIBLE
//    }
//
//    private fun showNoEventText(show: Boolean) {
//        binding.tvNoEventFavorite.visibility = if (show) View.VISIBLE else View.GONE
//        binding.rvEventFavorite.visibility = if (show) View.GONE else View.VISIBLE
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}