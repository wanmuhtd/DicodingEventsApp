package com.dicoding.wanmuhtd.dicodingeventsapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.wanmuhtd.dicodingeventsapp.data.model.ListEventsItem
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.ItemEventHorizontalBinding

class HomeEventAdapter(
    private val onClickItemListener: (ListEventsItem) -> Unit
): ListAdapter<ListEventsItem, HomeEventAdapter.EventViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event, onClickItemListener)
    }

    class EventViewHolder(private val binding: ItemEventHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem, onItemClickListener: (ListEventsItem) -> Unit) {
            Glide.with(itemView.context)
                .load(event.mediaCover)
                .into(binding.ivEventPicture)
            binding.tvEventTitle.text = event.name

            itemView.setOnClickListener {
                onItemClickListener(event)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}