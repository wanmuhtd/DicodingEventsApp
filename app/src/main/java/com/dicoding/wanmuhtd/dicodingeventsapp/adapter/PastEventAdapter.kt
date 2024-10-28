package com.dicoding.wanmuhtd.dicodingeventsapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.PastEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.ItemEventBinding

class PastEventAdapter(
    private val onClickItemListener: (PastEventsEntity) -> Unit
): ListAdapter<PastEventsEntity, PastEventAdapter.EventHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventHolder(binding)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event, onClickItemListener)
    }

    class EventHolder(private val binding: ItemEventBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(event: PastEventsEntity, onItemClickListener: (PastEventsEntity) -> Unit) {
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
        val DIFF_CALLBACK: DiffUtil.ItemCallback<PastEventsEntity> =
            object : DiffUtil.ItemCallback<PastEventsEntity>() {
                override fun areItemsTheSame(oldItem: PastEventsEntity, newItem: PastEventsEntity): Boolean {
                    return oldItem.name == newItem.name
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: PastEventsEntity, newItem: PastEventsEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}