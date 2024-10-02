package id.usereal.eventdicoding.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import id.usereal.eventdicoding.R
import id.usereal.eventdicoding.data.Event
import id.usereal.eventdicoding.databinding.ActivityDetailEventBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DetailEventActivity : AppCompatActivity() {
    companion object {
        const val EVENT_DETAIL = "event_detail"
    }

    private lateinit var binding: ActivityDetailEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val eventId = intent.getStringExtra(EVENT_DETAIL)
        if (eventId.isNullOrEmpty()) {
            Log.e("DetailEventActivity", "Event ID is null or empty")
            Toast.makeText(this, "Event ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            Log.d("DetailEventActivity", "Received Event ID: $eventId")
        }

        val viewModel = ViewModelProvider(this)[DetailEventViewModel::class.java]
        viewModel.fetchDetailEvent(eventId)  // Mengambil data event berdasarkan eventId

        viewModel.event.observe(this) { event ->
            if (event != null) {
                showEventDetail(event)  // Menampilkan detail event
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }


    private fun showEventDetail(event: Event) {
        Glide.with(this)
            .load(event.mediaCover)
            .into(binding.ivEventImage)
        Glide.with(this)
            .load(event.imageLogo)
            .override(350, 350)
            .into(binding.imageLogo)
        binding.tvEventName.text = event.name
        binding.tvEventSummary.text = event.summary
        binding.tvEventCategory.text = getString(R.string.kategori, event.category)

        binding.tvEventOwner.text = getString(R.string.penyelenggara, event.ownerName)

        binding.tvEventCity.text = getString(R.string.lokasi, event.cityName)
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val displayDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val displayTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        event.beginTime.let { beginTime ->
            val beginDate = beginTime?.let { apiDateFormat.parse(it) }
            binding.tvEventDate.text = getString(R.string.tanggal,
                beginDate?.let { displayDateFormat.format(it) })
            binding.tvEventTime.text = getString(R.string.waktu,
                beginDate?.let { displayTimeFormat.format(it) },
                event.endTime?.let { apiDateFormat.parse(it)?.let { displayTimeFormat.format(it) } })
        }

        binding.tvEventQuota.text = getString(R.string.kuota, "${event.quota?.minus(event.registrants!!)}")


//        binding.tvEventRegistrants.text = getString(R.string.pendaftar, event.registrants.toString())

        binding.tvEventDescription.text = HtmlCompat.fromHtml(
            event.description.toString(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.cardView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
}