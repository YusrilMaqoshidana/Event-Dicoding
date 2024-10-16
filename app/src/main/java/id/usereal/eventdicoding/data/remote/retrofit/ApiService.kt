package id.usereal.eventdicoding.data.remote.retrofit

import id.usereal.eventdicoding.data.remote.model.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events?active=1")
    suspend fun getUpcomingEvents(): EventResponse

    @GET("events?active=0")
    suspend fun getFinishedEvents(): EventResponse

    @GET("events")
    fun searchEvents(
        @Query("active") active: Int,
        @Query("q") query: String? = null
    ): EventResponse
}