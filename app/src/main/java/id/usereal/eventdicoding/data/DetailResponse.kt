package id.usereal.eventdicoding.data

import com.google.gson.annotations.SerializedName

data class  DetailResponse (
    @field:SerializedName("error")
    val error: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("event")
    val detailEvent: Event? = null
)