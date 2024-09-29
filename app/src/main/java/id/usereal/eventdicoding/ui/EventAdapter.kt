import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.usereal.eventdicoding.data.Event
import id.usereal.eventdicoding.databinding.CardItemEventBinding
import id.usereal.eventdicoding.ui.detailevent.DetailEvent

class EventAdapter(private val events: List<Event>) : ListAdapter<Event, EventAdapter.EventViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    class EventViewHolder(private val binding: CardItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            binding.titleTextView.text = event.name
            binding.descriptionTextView.text = event.description
            binding.quotaTextView.text = event.quota.toString()
            Glide.with(itemView.context)
                .load(event.imageLogo)
                .into(binding.itemImageView)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailEvent::class.java).apply {
                    putExtra(DetailEvent.EVENT_DETAIL, event.id.toString())
                }
                itemView.context.startActivity(intent)
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