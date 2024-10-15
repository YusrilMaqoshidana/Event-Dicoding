package id.usereal.eventdicoding.viewmodel
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import id.usereal.eventdicoding.data.Results
import id.usereal.eventdicoding.data.local.entity.EventEntity
import id.usereal.eventdicoding.data.repository.EventRepository
import kotlinx.coroutines.launch

class DetailEventViewModel(private val repository: EventRepository) : ViewModel() {

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite
    private val _event = MutableLiveData<Results<EventEntity>>()
    val event: LiveData<Results<EventEntity>> = _event

    fun getDetailById(eventId: String) {
        viewModelScope.launch {
            repository.getDetailById(eventId).observeForever { result ->
                _event.value = result
                if (result is Results.Success) {
                    checkFavoriteStatus(result.data.id)
                }
            }
        }
    }
    private fun checkFavoriteStatus(eventId: String) {
        viewModelScope.launch {
            repository.isEventFavorite(eventId).collect { isFavorite ->
                _isFavorite.value = isFavorite
            }
        }
    }
    fun toggleFavorite(event: EventEntity, context: Context) {
        viewModelScope.launch {
            val currentFavoriteStatus = _isFavorite.value ?: false
            if (currentFavoriteStatus) {
                repository.deleteFavoriteEvent(event.id)
                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
            } else {
                repository.addFavoriteEvent(event.id)
                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
            }
            _isFavorite.value = !currentFavoriteStatus
        }
    }
}