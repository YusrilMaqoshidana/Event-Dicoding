import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import id.usereal.eventdicoding.R
import id.usereal.eventdicoding.data.Event
import id.usereal.eventdicoding.databinding.ActivityDetailEventBinding
import id.usereal.eventdicoding.ui.detailevent.DetailEventViewModel

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
        val eventId = intent.getStringExtra(EVENT_DETAIL) ?: return finish()
        val viewModel = ViewModelProvider(this).get(DetailEventViewModel::class.java)
        viewModel.fetchDetailEvent(eventId)
        viewModel.eventDetail.observe(this) { event ->
            if (event != null) {
                showEventDetail(event)
            }
        }
    }

    private fun showEventDetail(event: Event) {
        // Set Gambar Event
        Glide.with(this)
            .load(event.mediaCover)
            .into(binding.ivEventImage)

        // Set Nama Event
        binding.tvEventName.text = event.name

        // Set Summary Event
        binding.tvEventSummary.text = event.summary

        // Set Kategori Event
        binding.tvEventCategory.text = getString(R.string.kategori, event.category)

        // Set Nama Penyelenggara
        binding.tvEventOwner.text = getString(R.string.penyelenggara, event.ownerName)

        // Set Kota Event
        binding.tvEventCity.text = getString(R.string.lokasi, event.cityName)

        // Set Tanggal Event
        binding.tvEventDate.text = getString(R.string.tanggal, event.beginTime)

        // Set Waktu Event
        binding.tvEventTime.text = getString(R.string.waktu, event.beginTime, event.endTime)

        // Set Kuota Event
        binding.tvEventQuota.text = getString(R.string.kuota, event.quota.toString())

        // Set Jumlah Pendaftar
        binding.tvEventRegistrants.text = getString(R.string.pendaftar, event.registrants.toString())

        // Set Deskripsi Event
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
