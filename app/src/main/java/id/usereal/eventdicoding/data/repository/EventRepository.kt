package id.usereal.eventdicoding.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import id.usereal.eventdicoding.data.Results
import id.usereal.eventdicoding.data.local.entity.EventEntity
import id.usereal.eventdicoding.data.local.entity.FavoriteEntity
import id.usereal.eventdicoding.data.local.room.EventDao
import id.usereal.eventdicoding.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
) {
    private val TAG = "EventRepository"

    fun getUpcomingEvent(): LiveData<Results<List<EventEntity>>> = liveData {
        emit(Results.Loading)
        Log.d(TAG, "Fetching upcoming events...")
        try {
            val databaseLocal = eventDao.getEventsActive()
            if (databaseLocal.isNotEmpty()) {
                Log.d(TAG, "Fetched ${databaseLocal.size} events from local database.")
                emit(Results.Success(databaseLocal))
            } else {
                Log.d(TAG, "No events found in local database. Fetching from API...")
                try {
                    val response = apiService.getUpcomingEvents()
                    val events = response.event
                    Log.d(TAG, "Fetched ${events.size} events from API.")
                    val eventList = events.map { event ->
                        EventEntity(
                            event.id,
                            event.name,
                            event.summary,
                            event.mediaCover,
                            event.registrants,
                            event.imageLogo,
                            event.link,
                            event.description,
                            event.ownerName,
                            event.cityName,
                            event.quota,
                            event.beginTime,
                            event.endTime,
                            event.category,
                            isActive = true
                        )
                    }
                    eventDao.deleteUpcomingEvents()
                    eventDao.insertEvents(eventList)
                    Log.d(TAG, "Inserted ${eventList.size} events into local database.")
                    emit(Results.Success(eventList))
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching events from API: ${e.message}")
                    emit(Results.Error("Gagal memproses data, cek koneksi Anda"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error accessing local database: ${e.message}")
            emit(Results.Error("Koneksi gagal, dan data lokal tidak ditemukan"))
        }
    }

    fun getFinishedEvent(): LiveData<Results<List<EventEntity>>> = liveData {
        emit(Results.Loading)
        Log.d(TAG, "Fetching finished events...")
        try {
            val databaseLocal = eventDao.getEventsNotActive()
            if (databaseLocal.isNotEmpty()) {
                Log.d(TAG, "Fetched ${databaseLocal.size} finished events from local database.")
                emit(Results.Success(databaseLocal))
            } else {
                Log.d(TAG, "No finished events found in local database. Fetching from API...")
                try {
                    val response = apiService.getFinishedEvents()
                    val events = response.event
                    val eventList = events.map { event ->
                        EventEntity(
                            id = event.id,
                            name = event.name,
                            summary = event.summary,
                            mediaCover = event.mediaCover,
                            registrants = event.registrants,
                            imageLogo = event.imageLogo,
                            link = event.link,
                            description = event.description,
                            ownerName = event.ownerName,
                            cityName = event.cityName,
                            quota = event.quota,
                            beginTime = event.beginTime,
                            endTime = event.endTime,
                            category = event.category,
                            isActive = false
                        )
                    }
                    Log.d(TAG, "Inserted ${events.size} finished events into local database.")
                    eventDao.deleteFinishedEvents()
                    eventDao.insertEvents(eventList)
                    Log.d(TAG, "Inserted ${eventList.size} finished events into local database.")
                    emit(Results.Success(eventList))
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching finished events from API: ${e.message}")
                    emit(Results.Error("Gagal memproses data, cek koneksi Anda"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error accessing local database: ${e.message}")
            emit(Results.Error("Koneksi gagal, dan data lokal tidak ditemukan"))
        }
    }

    fun searchEvents(active: Int, query: String): LiveData<Results<List<EventEntity>>> = liveData {
        emit(Results.Loading)
        Log.d(TAG, "Searching events with query: $query")
        try {
            val searchResult = eventDao.searchEvents(query, active)
            Log.d(TAG, "Search results: ${searchResult.size}")
            if (searchResult.isNotEmpty()) {
                emit(Results.Success(searchResult))
            } else {
                try {
                    val response = apiService.searchEvents(active, query)
                    val events = response.event
                    val eventList = events.map { event ->
                        EventEntity(
                            id = event.id,
                            name = event.name,
                            summary = event.summary,
                            mediaCover = event.mediaCover,
                            registrants = event.registrants,
                            imageLogo = event.imageLogo,
                            link = event.link,
                            description = event.description,
                            ownerName = event.ownerName,
                            cityName = event.cityName,
                            quota = event.quota,
                            beginTime = event.beginTime,
                            endTime = event.endTime,
                            category = event.category,
                            isActive = false
                        )
                    }
                    Log.d(TAG, "Inserted ${events.size} finished events into local database.")
                    eventDao.deleteFinishedEvents()
                    eventDao.insertEvents(eventList)
                    Log.d(TAG, "Inserted ${eventList.size} finished events into local database.")
                    emit(Results.Success(eventList))
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching finished events from API: ${e.message}")
                    emit(Results.Error("Event tidak ditemukan"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching events: ${e.message}")
            emit(Results.Error("Event tidak ditemukan"))
        }
    }


    fun getFavoriteEvents(): Flow<Results<List<EventEntity>>> {
        return eventDao.getFavoriteEvents()
            .map { Results.Success(it)}
            .catch { Results.Error(it.message ?: "An unknown error occurred") }
    }

    fun getDetailById(eventId: String): LiveData<Results<EventEntity>> = liveData {
        emit(Results.Loading)
        try {
            val event = eventDao.getEventById(eventId)
            emit(Results.Success(event))
        } catch (e: Exception) {
            emit(Results.Error(e.message ?: "An error occurred")) // Emit error
        }
    }


    suspend fun addFavoriteEvent(favoriteId: String) {
        val favorite = FavoriteEntity(favoriteId)
        eventDao.insertFavorite(favorite)
    }

    suspend fun deleteFavoriteEvent(favoriteId: String) {
        val favorite = FavoriteEntity(favoriteId)
        eventDao.deleteFavoriteEvent(favorite)
    }

    fun isEventFavorite(eventId: String): Flow<Boolean> {
        return eventDao.isEventFavorite(eventId)
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
