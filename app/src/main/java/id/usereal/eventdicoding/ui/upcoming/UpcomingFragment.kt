package id.usereal.eventdicoding.ui.upcoming

import EventAdapter
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import id.usereal.eventdicoding.data.Results
import id.usereal.eventdicoding.databinding.FragmentUpcomingBinding
import id.usereal.eventdicoding.viewmodel.EventViewModel
import kotlinx.coroutines.launch

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: EventAdapter
    private val eventViewModel: EventViewModel by viewModels { ViewModelFactory.getInstance(requireContext()) }
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

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
        setupNetworkCallback()
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

    private fun setupNetworkCallback() {
        connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewLifecycleOwner.lifecycleScope.launch {
                    eventViewModel.setNetworkState(true)
                    eventViewModel.getUpcomingEvent()
                }
            }

            override fun onLost(network: Network) {
                viewLifecycleOwner.lifecycleScope.launch {
                    eventViewModel.setNetworkState(false)
                }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun setupViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventViewModel.results.observeForever { result ->
                    when (result) {
                        is Results.Loading -> showLoading(true)
                        is Results.Success -> {
                            showLoading(false)
                            adapter.submitList(result.data)
                            showNoEventText(false)
                        }
                        is Results.Error -> {
                            showLoading(false)
                            showSnackbar(result.error)
                            showNoEventText(true)
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvEventUpcoming.visibility = if (isLoading) View.GONE else View.VISIBLE
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
        connectivityManager.unregisterNetworkCallback(networkCallback)
        _binding = null
    }
}