package id.usereal.eventdicoding.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import id.usereal.eventdicoding.data.local.entity.EventEntity
import id.usereal.eventdicoding.data.local.room.EventDao
import id.usereal.eventdicoding.data.remote.retrofit.ApiService
import id.usereal.eventdicoding.data.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.await

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
) {
    fun getAllEvent(): LiveData<Result<List<EventEntity>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getEvents(-1).await()
            val events = response.listEvents.mapNotNull { event ->
                event.id?.let { id ->
                    EventEntity(
                        id = event.id,
                        title = event.name ?: "No Title",
                        image = event.imageLogo ?: "No Link", // Assuming default values for missing fields
                        description = event.description ?: "No Description",
                        quota = (event.quota?.minus(event.registrants ?: 0)) ?: 0,
                        isFavorite = false
                    )
                }
            }

            eventDao.insertAll(events)
            emit(Result.Success(events))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Terjadi kesalahan"))
        }
    }

    fun getFavoriteEvent(): LiveData<Result<List<EventEntity>>> {
        return eventDao.getAllFavorite().map { favoriteEvents ->
            Result.Success(favoriteEvents)
        }
    }

    suspend fun setFavoriteEvent(event: EventEntity, favoriteState: Boolean) {
        withContext(Dispatchers.IO) {
            event.isFavorite = favoriteState
            eventDao.updateEvent(event)
        }
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(
            apiService: ApiService,
            eventDao: EventDao,
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, eventDao)
            }.also { instance = it }
    }
}