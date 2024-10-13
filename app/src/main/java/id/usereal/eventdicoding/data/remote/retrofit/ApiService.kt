package id.usereal.eventdicoding.data.remote.retrofit

import id.usereal.eventdicoding.data.remote.model.DetailEvent
import id.usereal.eventdicoding.data.remote.model.EventResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getEvents(@Query("active") active: Int): Call<EventResponse>

    @GET("events/{id}")
    fun getDetailEvent(@Path("id") id: Int): Call<DetailEvent>

    @GET("events")
    fun searchEvents(
        @Query("active") active: Int,
        @Query("q") query: String? = null
    ): Call<EventResponse>

}