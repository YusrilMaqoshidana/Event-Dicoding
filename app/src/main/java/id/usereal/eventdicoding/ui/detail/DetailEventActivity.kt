package id.usereal.eventdicoding.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import id.usereal.eventdicoding.R
import id.usereal.eventdicoding.data.Results
import id.usereal.eventdicoding.data.local.entity.EventEntity
import id.usereal.eventdicoding.databinding.ActivityDetailEventBinding
import id.usereal.eventdicoding.viewmodel.DetailEventViewModel
import id.usereal.eventdicoding.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private val viewModel: DetailEventViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val eventId = intent.getStringExtra(EVENT_DETAIL) ?: return showErrorAndFinish()

        viewModel.getDetailById(eventId)
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        binding.toolbarDetail.setNavigationIcon(R.drawable.ic_back)
        binding.toolbarDetail.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        viewModel.event.observe(this) { result ->
            when (result) {
                is Results.Loading -> showLoading(true)
                is Results.Success -> {
                    showLoading(false)
                    showEventDetail(result.data)
                    showNoDetail(false)
                }
                is Results.Error -> {
                    showLoading(false)
                    showNoDetail(true)
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.isFavorite.observe(this) { isFavorite ->
            updateFavoriteButton(isFavorite)
        }
    }

    private fun showErrorAndFinish() {
        Toast.makeText(this, "Event ID tidak ditemukan", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showEventDetail(event: EventEntity) {
        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val displayDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val displayTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val sisaQuota = event.quota?.minus(event.registrants ?: 0) ?: 0

        with(binding) {
            Glide.with(this@DetailEventActivity).load(event.mediaCover).into(ivEventImage)
            Glide.with(this@DetailEventActivity).load(event.imageLogo).override(350, 350).into(imageLogo)

            tvEventName.text = event.name
            tvEventSummary.text = event.summary
            tvEventCategory.text = getString(R.string.kategori, event.category)
            tvEventOwner.text = getString(R.string.penyelenggara, event.ownerName)
            tvEventCity.text = getString(R.string.lokasi, event.cityName)

            event.beginTime?.let { beginTime ->
                val beginDate = apiDateFormat.parse(beginTime)
                tvEventDate.text = getString(R.string.tanggal, beginDate?.let { displayDateFormat.format(it) })
                tvEventTime.text = getString(
                    R.string.waktu,
                    beginDate?.let { displayTimeFormat.format(it) },
                    event.endTime?.let { endTime ->
                        apiDateFormat.parse(endTime)?.let { displayTimeFormat.format(it) }
                    }
                )
            }

            tvEventQuota.text = if (sisaQuota > 0) {
                getString(R.string.kuota, sisaQuota.toString())
            } else {
                getString(R.string.kuota, "Habis")
            }

            tvEventDescription.text = HtmlCompat.fromHtml(event.description.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)

            btnRegister.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                startActivity(intent)
            }

            favoriteAdd.setOnClickListener {
                viewModel.toggleFavorite(event, this@DetailEventActivity)
            }
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        binding.favoriteAdd.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                if (isFavorite) R.drawable.ic_favorite_bold else R.drawable.ic_favorite
            )
        )
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
            cardView.visibility = if (showNoEvent) View.GONE else View.VISIBLE
            imageLogo.visibility = if (showNoEvent) View.GONE else View.VISIBLE
            btnRegister.visibility = if (showNoEvent) View.GONE else View.VISIBLE
        }
    }

    companion object {
        const val EVENT_DETAIL = "event_detail"
    }
}