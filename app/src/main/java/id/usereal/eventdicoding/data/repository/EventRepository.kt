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

    fun getUpcomingEvent(): LiveData<Results<List<EventEntity>>> = liveData {
        emit(Results.Loading)
        try {
            val databaseLocal = eventDao.getEventsActive()
            Log.d("EventRepository", "getUpcomingEvent - Local Data: $databaseLocal") // Log data lokal
            if (databaseLocal.isNotEmpty()) {
                emit(Results.Success(databaseLocal))
            } else {
                try {
                    val response = apiService.getUpcomingEvents()
                    val events = response.event.sortedBy { it.beginTime }
                    Log.d("EventRepository", "API Raw Response Upcoming: ${events.size}")
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
                    Log.d("EventRepository", "getUpcomingEvent - API Data: ${eventList.listIterator()}") // Log data dari API
                    eventDao.deleteUpcomingEvents()
                    eventDao.insertEvents(eventList)
                    emit(Results.Success(eventList))
                } catch (e: Exception) {
                    emit(Results.Error("Gagal memproses data, cek koneksi Anda"))
                }
            }
        } catch (e: Exception) {
            emit(Results.Error("Koneksi gagal, dan data lokal tidak ditemukan"))
        }
    }


    fun getFinishedEvent(): LiveData<Results<List<EventEntity>>> = liveData {
        emit(Results.Loading)
        try {
            val databaseLocal = eventDao.getEventsNotActive()
            Log.d("EventRepository", "getFinishedEvent - Local Data: $databaseLocal") // Log data lokal
            if (databaseLocal.isNotEmpty()) {
                emit(Results.Success(databaseLocal))
            } else {
                try {
                    val response = apiService.getFinishedEvents()
                    val events = response.event
                    Log.d("EventRepository", "API Raw Response Finished: ${events.size}")
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
                    Log.d("EventRepository", "getFinishedEvent - API Data: $eventList") // Log data dari API
                    eventDao.deleteFinishedEvents()
                    eventDao.insertEvents(eventList)
                    emit(Results.Success(eventList))
                } catch (e: Exception) {
                    emit(Results.Error("Gagal memproses data, cek koneksi Anda"))
                }
            }
        } catch (e: Exception) {
            emit(Results.Error("Koneksi gagal, dan data lokal tidak ditemukan"))
        }
    }


    fun searchEvents(active: Int, query: String): LiveData<Results<List<EventEntity>>> = liveData {
        emit(Results.Loading)
        try {
            val searchResult = eventDao.searchEvents(query, active)
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
                    eventDao.deleteFinishedEvents()
                    eventDao.insertEvents(eventList)
                    emit(Results.Success(eventList))
                } catch (e: Exception) {
                    emit(Results.Error("Event tidak ditemukan"))
                }
            }
        } catch (e: Exception) {
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

    suspend fun getNotifEvent(): EventEntity? {
        val closestEvent = eventDao.getClosestActiveEvent(System.currentTimeMillis())
        return closestEvent
    }
}
