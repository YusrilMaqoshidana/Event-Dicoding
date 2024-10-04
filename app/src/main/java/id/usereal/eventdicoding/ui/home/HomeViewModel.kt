package id.usereal.eventdicoding.ui.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.usereal.eventdicoding.data.Event
import id.usereal.eventdicoding.data.EventResponse
import id.usereal.eventdicoding.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeViewModel : ViewModel(){
    private val _eventSearch = MutableLiveData<List<Event>>()
    val eventSearch: LiveData<List<Event>> = _eventSearch

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> = _snackbarMessage

    fun searchEvent(query: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchEvents(-1, query)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.listEvents?.let {
                        _eventSearch.value = it
                    }
                } else {
                    _eventSearch.value = emptyList()
                    _snackbarMessage.value = "Tidak ada hasil pencarian"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _snackbarMessage.value = "Gagal Mengakses Konten"
            }

        })
    }

}



