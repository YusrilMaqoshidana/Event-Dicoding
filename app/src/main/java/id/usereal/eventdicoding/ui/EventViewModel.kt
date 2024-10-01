package id.usereal.eventdicoding.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.usereal.eventdicoding.data.Event
import id.usereal.eventdicoding.data.EventResponse
import id.usereal.eventdicoding.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _showNoEvent = MutableLiveData<Boolean>()
    val showNoEvent: LiveData<Boolean> = _showNoEvent

    fun fetchFinishedEvents(){
        _isLoading.value = true
        _showNoEvent.value = false
        val client = ApiConfig.getApiService().getEvents(active = 0)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _events.value = response.body()?.listEvents
                    _showNoEvent.value = response.body()?.listEvents?.isEmpty() == true
                }else{
                    Log.e("Finished", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Log.e("Finished", "onFailure: ${t.message.toString()}")
            }

        })
    }

    fun fetchUpcomingEvents() {
        _isLoading.value = true
        _showNoEvent.value = false
        val client = ApiConfig.getApiService().getEvents(active = 1)
        client.enqueue(object : Callback<EventResponse>{
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _events.value = response.body()?.listEvents
                    _showNoEvent.value = response.body()?.listEvents?.isEmpty() == true
                }else{
                    Log.e("Upcoming", "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("Upcoming", "onFailure: ${t.message.toString()}")
            }
        })
    }
}