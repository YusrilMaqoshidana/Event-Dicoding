package id.usereal.eventdicoding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.usereal.eventdicoding.data.remote.model.DetailEvent
import id.usereal.eventdicoding.data.remote.model.Event
import id.usereal.eventdicoding.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEventViewModel : ViewModel() {
    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _showNoEvent = MutableLiveData<Boolean>()
    val showNoEvent: LiveData<Boolean> = _showNoEvent

    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> = _snackbarMessage

    fun fetchDetailEvent(eventId: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvent(eventId.toInt())
        client.enqueue(object : Callback<DetailEvent> {
            override fun onResponse(call: Call<DetailEvent>, response: Response<DetailEvent>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val data = response.body()?.event
                    if (data != null) {
                        _event.value = data
                    } else {
                        _showNoEvent.value = true
                        _snackbarMessage.value = "Data tidak ditemukan"
                    }
                } else {
                    _snackbarMessage.value = "Gagal mendapatkan data: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<DetailEvent>, t: Throwable) {
                _isLoading.value = false
                _showNoEvent.value = true
                _snackbarMessage.value = "Gagal Mengakses Konten"
            }

        })

    }
}
