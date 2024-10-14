package id.usereal.eventdicoding.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import id.usereal.eventdicoding.data.Results
import id.usereal.eventdicoding.data.local.entity.EventEntity
import id.usereal.eventdicoding.data.local.entity.FavoriteEntity
import id.usereal.eventdicoding.data.local.room.EventDao
import id.usereal.eventdicoding.data.remote.retrofit.ApiService

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
) {
    fun getUpcomingEvent(): LiveData<Results<List<EventEntity>>> = liveData {
        emit(Results.Loading)
        try {
            val databaseLocal = eventDao.getEventsActive()
            if (databaseLocal.isNotEmpty()) {
                emit(Results.Success(databaseLocal))
            } else {
                try {
                    val response = apiService.getEvents(1)
                    val events = response.listEvents
                    val eventList = events.map { event: EventEntity ->
                        EventEntity(
                            id = event.id,
                            name = event.name,
                            summary = event.summary,
                            mediaCover = event.mediaCover,        // Sesuai dengan urutan
                            registrants = event.registrants,                   // Tidak ada pada parameter
                            imageLogo = event.imageLogo,          // Sesuai dengan urutan
                            link = event.link,                    // Sesuai dengan urutan
                            description = event.description,      // Sesuai dengan urutan
                            ownerName = event.ownerName,          // Sesuai dengan urutan
                            cityName = event.cityName,            // Sesuai dengan urutan
                            quota = event.quota,                         // Tidak ada pada parameter
                            beginTime = event.beginTime,          // Sesuai dengan urutan
                            endTime = event.endTime,                       // Tidak ada pada parameter
                            category = event.category,            // Sesuai dengan urutan
                            isActive = true                       // Tidak ada pada parameter
                        )
                    }
                    eventDao.deleteUpcomingEvents()
                    eventDao.insertEvents(eventList)
                    emit(Results.Success(eventList))
                } catch (e: Exception) {
                    emit(Results.Error("Gagal memproses data cek koneksi anda"))
                }
            }
        } catch (e: Exception) {
            emit(Results.Error("Koneksi gagal dan data local tidak ditemukan"))
        }
    }

    fun getFinishedEvent(): LiveData<Results<List<EventEntity>>> = liveData {
        emit(Results.Loading)
        try {
            val databaseLocal = eventDao.getEventsNotActive()
            if (databaseLocal.isNotEmpty()) {
                emit(Results.Success(databaseLocal))
            } else {
                try {
                    val response = apiService.getEvents(0)
                    val events = response.listEvents
                    val eventList = events.map { event: EventEntity ->
                        EventEntity(
                            id = event.id,
                            name = event.name,
                            summary = event.summary,
                            mediaCover = event.mediaCover,        // Sesuai dengan urutan
                            registrants = event.registrants,                   // Tidak ada pada parameter
                            imageLogo = event.imageLogo,          // Sesuai dengan urutan
                            link = event.link,                    // Sesuai dengan urutan
                            description = event.description,      // Sesuai dengan urutan
                            ownerName = event.ownerName,          // Sesuai dengan urutan
                            cityName = event.cityName,            // Sesuai dengan urutan
                            quota = event.quota,                         // Tidak ada pada parameter
                            beginTime = event.beginTime,          // Sesuai dengan urutan
                            endTime = event.endTime,                       // Tidak ada pada parameter
                            category = event.category,            // Sesuai dengan urutan
                            isActive = false                       // Tidak ada pada parameter
                        )
                    }
                    eventDao.deleteFinishedEvents()
                    eventDao.insertEvents(eventList)
                    emit(Results.Success(eventList))
                } catch (e: Exception) {
                    emit(Results.Error("Gagal memproses data cek koneksi anda"))
                }
            }
        } catch (e: Exception) {
            emit(Results.Error("Koneksi gagal dan data local tidak ditemukan"))
        }
    }

    fun searchEvents(query: String, isActive: Boolean): LiveData<Results<List<EventEntity>>> = liveData {
        emit(Results.Loading)
        try {

            val databaseLocal = eventDao.searchEvents(query, isActive)
            if (databaseLocal.isNotEmpty()) {
                emit(Results.Success(databaseLocal))
            } else {
                try {
                    val isActiveInt = if (isActive) 1 else 0
                    val response = apiService.searchEvents(isActiveInt, query)
                    val events = response.listEvents
                    val eventList = events.map { event: EventEntity ->
                        EventEntity(
                            id = event.id,
                            name = event.name,
                            summary = event.summary,
                            mediaCover = event.mediaCover,        // Sesuai dengan urutan
                            registrants = event.registrants,                   // Tidak ada pada parameter
                            imageLogo = event.imageLogo,          // Sesuai dengan urutan
                            link = event.link,                    // Sesuai dengan urutan
                            description = event.description,      // Sesuai dengan urutan
                            ownerName = event.ownerName,          // Sesuai dengan urutan
                            cityName = event.cityName,            // Sesuai dengan urutan
                            quota = event.quota,                         // Tidak ada pada parameter
                            beginTime = event.beginTime,          // Sesuai dengan urutan
                            endTime = event.endTime,                       // Tidak ada pada parameter
                            category = event.category,            // Sesuai dengan urutan
                            isActive = isActive                       // Tidak ada pada parameter
                        )
                    }
                    eventDao.deleteFinishedEvents()
                    eventDao.insertEvents(eventList)
                    emit(Results.Success(eventList))
                } catch (e: Exception) {
                    emit(Results.Error("Gagal memproses data cek koneksi anda"))
                }
            }
        } catch (e: Exception) {
            emit(Results.Error("Koneksi gagal dan data local tidak ditemukan"))
        }
    }

    fun getFavoriteEvents(): LiveData<Results<List<EventEntity>>> = liveData {
        emit(Results.Loading)
        try {
            val favoriteEvents = eventDao.getFavoriteEvents()
            emit(Results.Success(favoriteEvents))
        } catch (e: Exception) {
            emit(Results.Error("Favorite Event tidak ditemukan"))
        }
    }

//    fun getDetailById(eventId: String): LiveData<Results<EventEntity>> = liveData {
//        try {
//            val detailEvent = eventDao.getEventById(eventId)
//            emit(Results.Success(detailEvent))
//        } catch (e: Exception) {
//            emit(Results.Error("Gagal memproses data: ${e.message}"))
//        }
//    }

    suspend fun addFavoriteEvent(favorite: FavoriteEntity) {
        eventDao.insertFavorite(favorite)
    }

    suspend fun deleteFavoriteEvent(favorite: FavoriteEntity) {
        eventDao.deleteFavoriteEvent(favorite)
    }

    fun isEventFavorite(eventId: String): Boolean {
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