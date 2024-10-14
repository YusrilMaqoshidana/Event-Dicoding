package id.usereal.eventdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.usereal.eventdicoding.data.Results
import id.usereal.eventdicoding.data.local.entity.EventEntity
import id.usereal.eventdicoding.data.repository.EventRepository
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {
    private val _results = MutableLiveData<Results<List<EventEntity>>>(Results.Loading)
    val results: LiveData<Results<List<EventEntity>>> = _results

    private val _network = MutableLiveData<Boolean>()
    val network: LiveData<Boolean> = _network

    fun getUpcomingEvent() {
        repository.getUpcomingEvent().observeForever { event ->
            viewModelScope.launch {
                _results.value = event
            }
        }

    }

    fun getFinishedEvent() {
        repository.getFinishedEvent().observeForever{ event ->
            viewModelScope.launch {
                _results.value = event
            }

        }
    }

    fun getFavoriteEvent() {
        repository.getFavoriteEvents().observeForever { event ->
            viewModelScope.launch {
                _results.value = event
            }
        }
    }

    fun searchEvent(query: String, isActive: Boolean) {
        repository.searchEvents(query, isActive).observeForever { event ->
            viewModelScope.launch {
                _results.value = event
            }
        }
    }

    fun setNetworkState(isConnected: Boolean) {
        _network.value = isConnected
    }

}