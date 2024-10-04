package id.usereal.eventdicoding.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.usereal.eventdicoding.data.Event
import id.usereal.eventdicoding.databinding.CardItemHomeBinding
import id.usereal.eventdicoding.ui.detail.DetailEventActivity

class HomeAdapter : ListAdapter<Event, HomeAdapter.HomeEventViewHolder>(DIFF_CALLBACK) {

    class HomeEventViewHolder(private val binding: CardItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            with(binding){
                Glide.with(itemView.context)
                    .load(event.imageLogo)
                    .into(imageView)

                textView.text = event.name

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeEventViewHolder {
        val binding = CardItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeEventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
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
