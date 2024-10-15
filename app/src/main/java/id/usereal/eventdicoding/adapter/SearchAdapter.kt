import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.usereal.eventdicoding.data.local.entity.EventEntity
import id.usereal.eventdicoding.databinding.CardFavoriteEventBinding
import id.usereal.eventdicoding.ui.detail.DetailEventActivity

class SearchAdapter : ListAdapter<EventEntity, SearchAdapter.SearchViewHolder>(DIFF_CALLBACK) {

    class SearchViewHolder(private val binding: CardFavoriteEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventEntity) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(event.imageLogo)
                    .into(itemImageLogo)

                titleTextView.text = event.name
                descriptionTextView.text = event.description

                quotaTextView.text = event.quota?.let { quota ->
                    String.format("Jumlah kuota: %s", (event.quota - (event.registrants ?: 0)))
                } ?: "Jumlah kuota: -"

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailEventActivity::class.java).apply {
                        putExtra(DetailEventActivity.EVENT_DETAIL, event.id)
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = CardFavoriteEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
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
