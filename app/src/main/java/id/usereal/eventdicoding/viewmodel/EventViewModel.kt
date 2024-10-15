package id.usereal.eventdicoding.viewmodel


import androidx.lifecycle.ViewModel
import id.usereal.eventdicoding.data.repository.EventRepository

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    fun getUpcomingEvents() = repository.getUpcomingEvent()
    fun getFinishedEvents() = repository.getFinishedEvent()
    fun searchEvent(query: String, isActive: Int) = repository.searchEvents(isActive, query)

}