import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.usereal.eventdicoding.R
import id.usereal.eventdicoding.data.local.entity.EventEntity
import id.usereal.eventdicoding.databinding.CardEventUpcomingBinding
import id.usereal.eventdicoding.ui.detail.DetailEventActivity

class EventAdapter : ListAdapter<EventEntity, EventAdapter.EventViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding =
            CardEventUpcomingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    class EventViewHolder(private val binding: CardEventUpcomingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity) {
            with(binding) {
                titleTextView.text = event.name
                descriptionTextView.text = HtmlCompat.fromHtml(
                    event.description ?: "",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )

                val sisaQuota = event.quota?.minus(event.registrants ?: 0) ?: 0

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
                event.id.let { id ->
                    val intent = Intent(itemView.context, DetailEventActivity::class.java).apply {
                        putExtra(DetailEventActivity.EVENT_DETAIL, id)
                    }
                        Log.d("EventAdapter", "Event ID: $intent")
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventEntity>() {
            override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}