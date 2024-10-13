package id.usereal.eventdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.usereal.eventdicoding.data.remote.model.Event
import id.usereal.eventdicoding.data.remote.model.EventResponse
import id.usereal.eventdicoding.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingViewModel : ViewModel() {
    private val _eventsUpcoming = MutableLiveData<List<Event>>()
    val eventsUpcoming: LiveData<List<Event>> = _eventsUpcoming

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _showNoEvent = MutableLiveData<Boolean>()
    val showNoEvent: LiveData<Boolean> = _showNoEvent

    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> = _snackbarMessage

    fun fetchApiUpcoming() {
        _isLoading.value = true
        _showNoEvent.value = false
        val client = ApiConfig.getApiService().getEvents(1)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful ) {
                    val data = response.body()?.listEvents
                    _eventsUpcoming.value = data!!
                    _showNoEvent.value = data.isEmpty() == true
                } else {
                    _snackbarMessage.value = "Tidak ada data"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _showNoEvent.value = true
                _eventsUpcoming.value = emptyList()
                _snackbarMessage.value = "Gagal Mengakses Konten"
            }

        })
    }
}