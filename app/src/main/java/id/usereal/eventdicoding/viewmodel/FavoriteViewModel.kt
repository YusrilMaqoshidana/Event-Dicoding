package id.usereal.eventdicoding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.usereal.eventdicoding.data.Results
import id.usereal.eventdicoding.data.repository.EventRepository
import id.usereal.eventdicoding.data.local.entity.EventEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {

    private val _favoriteEvents = MutableStateFlow<Results<List<EventEntity>>>(Results.Loading)
    val favoriteEvents: StateFlow<Results<List<EventEntity>>> = _favoriteEvents

    init {
        loadFavoriteEvents()
    }

    fun loadFavoriteEvents() {
        viewModelScope.launch {
            repository.getFavoriteEvents().collect { result ->
                _favoriteEvents.value = result
            }
        }
    }
}