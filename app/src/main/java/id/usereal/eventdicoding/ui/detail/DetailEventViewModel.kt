package id.usereal.eventdicoding.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.usereal.eventdicoding.data.DetailEvent
import id.usereal.eventdicoding.data.Event
import id.usereal.eventdicoding.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEventViewModel : ViewModel() {
    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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
                        Log.d("TAG: onResponseDetail", "onResponse: $data")
                    } else {
                        Log.d("TAG: onResponse", "Data tidak ditemukan $data")
                        // Handle the case when data is null, e.g. show an error message
                    }
                } else {
                    _isLoading.value = true
                    Log.d("TAG: onResponse", "onFailure: ${response.message()}")
                }
            }


            override fun onFailure(call: Call<DetailEvent>, t: Throwable) {
                Log.d("TAG: onFailure", "onFailure: ${t.message.toString()}")
            }
        })

    }
}
