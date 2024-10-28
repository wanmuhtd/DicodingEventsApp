package com.dicoding.wanmuhtd.dicodingeventsapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.UpcomingEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.ItemEventHorizontalBinding

class HomeEventAdapter(
    private val onClickItemListener: (UpcomingEventsEntity) -> Unit
): ListAdapter<UpcomingEventsEntity, HomeEventAdapter.EventHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val binding = ItemEventHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventHolder(binding)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event, onClickItemListener)
    }

    class EventHolder(private val binding: ItemEventHorizontalBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(event: UpcomingEventsEntity, onItemClickListener: (UpcomingEventsEntity) -> Unit) {
            Glide.with(binding.root.context)
                .load(event.mediaCover)
                .into(binding.ivEventPicture)
            binding.tvEventTitle.text = event.name
            binding.root.setOnClickListener {
                onItemClickListener(event)
            }
        }
    }

    companion object{
        val DIFF_CALLBACK: DiffUtil.ItemCallback<UpcomingEventsEntity> =
            object : DiffUtil.ItemCallback<UpcomingEventsEntity>() {
                override fun areItemsTheSame(oldItem: UpcomingEventsEntity, newItem: UpcomingEventsEntity): Boolean {
                    return oldItem.name == newItem.name
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: UpcomingEventsEntity, newItem: UpcomingEventsEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}