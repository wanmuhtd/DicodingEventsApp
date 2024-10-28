package com.dicoding.wanmuhtd.dicodingeventsapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.wanmuhtd.dicodingeventsapp.data.local.entity.FavoriteEventsEntity
import com.dicoding.wanmuhtd.dicodingeventsapp.databinding.ItemEventBinding

class FavoriteEventAdapter(
    private val onClickItemListener: (FavoriteEventsEntity) -> Unit
): ListAdapter<FavoriteEventsEntity, FavoriteEventAdapter.EventHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventHolder(binding)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event, onClickItemListener)
    }

    class EventHolder(private val binding: ItemEventBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(event: FavoriteEventsEntity, onItemClickListener: (FavoriteEventsEntity) -> Unit) {
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
        val DIFF_CALLBACK: DiffUtil.ItemCallback<FavoriteEventsEntity> =
            object : DiffUtil.ItemCallback<FavoriteEventsEntity>() {
                override fun areItemsTheSame(oldItem: FavoriteEventsEntity, newItem: FavoriteEventsEntity): Boolean {
                    return oldItem.name == newItem.name
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: FavoriteEventsEntity, newItem: FavoriteEventsEntity): Boolean {
                    return oldItem == newItem
                }
            }
    }
}