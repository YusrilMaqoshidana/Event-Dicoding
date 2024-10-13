package id.usereal.eventdicoding.data.remote.model

import com.google.gson.annotations.SerializedName

data class EventResponse(

	@field:SerializedName("listEvents")
	val listEvents: List<Event> = listOf(),

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)


