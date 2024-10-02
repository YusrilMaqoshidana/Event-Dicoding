import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.usereal.eventdicoding.ui.detail.DetailEventActivity
import id.usereal.eventdicoding.data.Event
import id.usereal.eventdicoding.databinding.CardItemEventBinding

class EventAdapter : ListAdapter<Event, EventAdapter.EventViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    class EventViewHolder(private val binding: CardItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            binding.titleTextView.text = event.name
            binding.descriptionTextView.text = HtmlCompat.fromHtml(
                event.description.toString(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            binding.quotaTextView.text = String.format("Sisa Kuota: %s/%s",  event.registrants.toString(), event.quota.toString())
            Glide.with(itemView.context)
                .load(event.imageLogo)
                .into(binding.itemImageView)

            itemView.setOnClickListener {
                event.id?.let { id ->
                    val intent = Intent(itemView.context, DetailEventActivity::class.java).apply {
                        putExtra(DetailEventActivity.EVENT_DETAIL, id.toString()) // Pastikan ID dalam bentuk String
                    }
                    itemView.context.startActivity(intent)
                } ?: Log.e("EventAdapter", "Event ID is null")
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
