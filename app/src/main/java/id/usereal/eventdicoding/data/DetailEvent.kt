package id.usereal.eventdicoding.data

import com.google.gson.annotations.SerializedName

data class DetailEvent(

    @field:SerializedName("event")
    val event: Event,

    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)