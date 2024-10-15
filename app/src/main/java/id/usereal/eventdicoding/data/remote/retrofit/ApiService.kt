package id.usereal.eventdicoding.data.remote.retrofit

import id.usereal.eventdicoding.data.remote.model.DetailEvent
import id.usereal.eventdicoding.data.remote.model.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events?active=1")
    suspend fun getUpcomingEvents(): EventResponse

    @GET("events?active=0")
    suspend fun getFinishedEvents(): EventResponse

    @GET("events/{id}")
    suspend fun getDetailEvent(@Path("id") id: String): DetailEvent

}