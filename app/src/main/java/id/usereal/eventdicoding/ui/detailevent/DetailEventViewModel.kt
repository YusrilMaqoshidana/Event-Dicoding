package id.usereal.eventdicoding.ui.detailevent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.usereal.eventdicoding.data.DetailResponse
import id.usereal.eventdicoding.data.Event
import id.usereal.eventdicoding.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEventViewModel : ViewModel() {
    private val _eventDetail = MutableLiveData<Event?>()
    val eventDetail: LiveData<Event?> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    fun fetchDetailEvent(eventId: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvent(eventId)

        client.enqueue(object : Callback<DetailResponse> {

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Error occurred: ${t.message}"
            }

            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val detailEvent = response.body()
                    Log.d("DetailEventViewModel", "Detail Event: $detailEvent")
                    if (detailEvent != null) {
                        _eventDetail.value = detailEvent.detailEvent
                    } else {
                        _errorMessage.value = "Event detail is empty"
                    }
                } else {
                    _errorMessage.value = "Failed to fetch event detail: ${response.code()}"
                }
            }
        })
    }
}
