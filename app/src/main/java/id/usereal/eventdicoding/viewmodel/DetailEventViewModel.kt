package id.usereal.eventdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.usereal.eventdicoding.data.Results
import id.usereal.eventdicoding.data.local.entity.EventEntity
import id.usereal.eventdicoding.data.local.entity.FavoriteEntity
import id.usereal.eventdicoding.data.remote.model.Event
import id.usereal.eventdicoding.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class DetailEventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _results = MutableLiveData<Results<EventEntity>>(Results.Loading)
    val results: LiveData<Results<EventEntity>> = _results

    // Fetch event details by ID
    fun fetchDetailEvent(eventId: String) {
        repository.getDetailById(eventId).observeForever { result ->
            viewModelScope.launch {
                _results.value = result
                checkFavoriteStatus(eventId)
            }
        }
    }

    // Check if the event is in the favorites list
    private fun checkFavoriteStatus(eventId: String) {
        viewModelScope.launch {
            val isFavorited = repository.isEventFavorite(eventId)
            _isFavorite.postValue(isFavorited)
        }
    }

    fun toggleFavorite() {
        val currentEvent = (_results.value as? Results.Success)?.data ?: return
        val currentFavoriteStatus = _isFavorite.value ?: false

        viewModelScope.launch {
            if (currentFavoriteStatus) {
                repository.deleteFavoriteEvent(
                    FavoriteEntity(
                        id = currentEvent.id.toString()
                    )
                )
            } else {
                repository.addFavoriteEvent(
                    FavoriteEntity(
                        id = currentEvent.id.toString()
                    )
                )
            }
            _isFavorite.value = !currentFavoriteStatus
        }
    }
}
