package id.usereal.eventdicoding.ui

import EventAdapter
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

    fun fetchFinishedEvents() {
        _isLoading.value = true
        _showNoEvent.value = false
        val client = ApiConfig.getApiService().getEvents(active = 0)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val data = response.body()?.listEvents
                    _events.value = data!!
                    _showNoEvent.value = data.isEmpty() == true
                    Log.d("TAG: onRespone", "onResponse: $data")
                } else {
                    Log.d("Finished", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                Log.d("TAG: onFailure", "onFailure: ${t.message.toString()}")
            }

        })
    }

    fun fetchUpcomingEvents() {
        _isLoading.value = true
        _showNoEvent.value = false
        val client = ApiConfig.getApiService().getEvents(active = 1)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val data = response.body()?.listEvents
                    _events.value = data!!
                    _showNoEvent.value = data.isEmpty() == true
                    Log.d("TAG: onRespone", "onResponse: $data")
                } else {
                    Log.e("TAG: onRespone", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e("TAG: onFailure", "onFailure: ${t.message.toString()}")
            }
        })
    }
}