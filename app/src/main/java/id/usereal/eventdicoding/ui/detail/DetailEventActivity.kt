package id.usereal.eventdicoding.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import id.usereal.eventdicoding.R
import id.usereal.eventdicoding.data.Event
import id.usereal.eventdicoding.databinding.ActivityDetailEventBinding
import java.text.SimpleDateFormat
import java.util.Locale

class DetailEventActivity : AppCompatActivity() {


    private lateinit var binding: ActivityDetailEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarDetail)
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding.toolbarDetail.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarDetail.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val eventId = intent.getStringExtra(EVENT_DETAIL)
        if (eventId.isNullOrEmpty()) {
            Toast.makeText(this, "Event ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val viewModel = ViewModelProvider(this)[DetailEventViewModel::class.java]
        viewModel.fetchDetailEvent(eventId)
        viewModel.event.observe(this) { event ->
            if (event != null) {
                showEventDetail(event)
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
        viewModel.snackbarMessage.observe(this) { message ->
            message?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }
        viewModel.showNoEvent.observe(this) { noEvent ->

            showNoDetail(noEvent)
        }
    }


    private fun showEventDetail(event: Event) {
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val displayDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val displayTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val sisaQuota = event.quota?.minus(event.registrants!!) ?: 0
        with(binding) {
            Glide.with(root.context)
                .load(event.mediaCover)
                .into(ivEventImage)
            Glide.with(root.context)
                .load(event.imageLogo)
                .override(350, 350)
                .into(imageLogo)
            tvEventName.text = event.name
            tvEventSummary.text = event.summary
            tvEventCategory.text = getString(R.string.kategori, event.category)
            tvEventOwner.text = getString(R.string.penyelenggara, event.ownerName)
            tvEventCity.text = getString(R.string.lokasi, event.cityName)
            event.beginTime.let { beginTime ->
                val beginDate = beginTime?.let { apiDateFormat.parse(it) }
                tvEventDate.text = getString(R.string.tanggal,
                    beginDate?.let { displayDateFormat.format(it) })
                tvEventTime.text = getString(R.string.waktu,
                    beginDate?.let { displayTimeFormat.format(it) },
                    event.endTime?.let { it ->
                        apiDateFormat.parse(it)?.let { displayTimeFormat.format(it) }
                    })
            }
            tvEventQuota.text = if (sisaQuota > 0) {
                getString(R.string.kuota, sisaQuota.toString())
            } else {
                getString(R.string.kuota, "Habis")
            }
            tvEventDescription.text = HtmlCompat.fromHtml(
                event.description.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            btnRegister.setOnClickListener {
                val uri = Uri.parse("${event.link}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            cardView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun showNoDetail(showNoEvent: Boolean) {
        with(binding) {
            tvNoDetailEvent.visibility = if (showNoEvent) View.VISIBLE else View.GONE
            tvNoDetailEvent.gravity = Gravity.CENTER
            progressBar.visibility = View.GONE
            cardView.visibility = View.GONE
            imageLogo.visibility = View.GONE
            btnRegister.visibility = View.GONE
        }
    }

    companion object {
        const val EVENT_DETAIL = "event_detail"
    }
}