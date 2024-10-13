package id.usereal.eventdicoding.ui.finished

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.usereal.eventdicoding.R
import id.usereal.eventdicoding.ui.detail.DetailEventActivity
import id.usereal.eventdicoding.data.remote.model.Event
import id.usereal.eventdicoding.databinding.CardEventFinishedBinding

class FinishedAdapter : ListAdapter<Event, FinishedAdapter.EventViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding =
            CardEventFinishedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    class EventViewHolder(private val binding: CardEventFinishedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            with(binding) {
                titleTextView.text = event.name
                descriptionTextView.text = HtmlCompat.fromHtml(
                    event.description.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

                val sisaQuota = event.quota?.minus(event.registrants!!) ?: 0

                quotaTextView.text = if (sisaQuota > 0) {
                    itemView.context.getString(R.string.kuota, sisaQuota.toString())
                } else {
                    itemView.context.getString(R.string.kuota, "Habis")
                }

                Glide.with(itemView.context)
                    .load(event.imageLogo)
                    .into(itemImageLogo)

            }
            itemView.setOnClickListener {
                event.id?.let { id ->
                    val intent = Intent(itemView.context, DetailEventActivity::class.java).apply {
                        putExtra(DetailEventActivity.EVENT_DETAIL, id.toString())
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem == newItem
            }
        }
    }
}
