package id.usereal.eventdicoding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.usereal.eventdicoding.data.local.entity.EventEntity
import id.usereal.eventdicoding.repository.EventRepository
import kotlinx.coroutines.launch

class FavoriteViewModel(private val eventRepository: EventRepository) : ViewModel() {
    fun getAllEvent() = eventRepository.getAllEvent()
    fun getFavoriteEvent() = eventRepository.getFavoriteEvent()
    fun setFavoriteEvent(event: EventEntity, favoriteState: Boolean) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvent(event, favoriteState)
        }
    }
}