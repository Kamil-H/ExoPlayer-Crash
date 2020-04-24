package com.nomtek.exoplayercrash.custom_views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nomtek.exoplayercrash.R
import kotlinx.android.synthetic.main.view_player_playlist_item.view.*

class PlaylistAdapter(
    private val callback: (Int) -> Unit
) : ListAdapter<PlaylistItem, PlaylistAdapter.ItemViewViewHolder>(ItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewViewHolder {
        return ItemViewViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_player_playlist_item, parent, false)).apply {
            itemView.setOnClickListener {
                callback(adapterPosition)
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: PlaylistItem?) {
            item ?: return
            view.titleTextView.text = item.title
            view.descriptionTextView.text = item.subtitle
            view.setBackgroundResource(if (item.isPlaying) R.color.player_list_playing_item_background else 0)
            view.rightTextView.isVisible = item.isRightText
        }
    }

    private class ItemCallback : DiffUtil.ItemCallback<PlaylistItem>() {
        override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
            return oldItem == newItem
        }
    }
}
