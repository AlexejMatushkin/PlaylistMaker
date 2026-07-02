package com.practicum.playlistmaker.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistSmallViewBinding
import com.practicum.playlistmaker.domain.playlist.model.Playlist

class PlaylistMenuAdapter : RecyclerView.Adapter<PlaylistMenuAdapter.ViewHolder>() {

    private var playlist: Playlist? = null

    fun setPlaylist(playlist: Playlist?) {
        this.playlist = playlist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaylistSmallViewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(playlist ?: return)
    }

    override fun getItemCount(): Int = if (playlist != null) 1 else 0

    class ViewHolder(private val binding: PlaylistSmallViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: Playlist) {
            binding.playlistSmallName.text = playlist.name
            val tracks = when {
                playlist.count % 10 == 1 && playlist.count % 100 != 11 -> "трек"
                playlist.count % 10 in 2..4 && playlist.count % 100 !in 12..14 -> "трека"
                else -> "треков"
            }
            binding.playlistSmallCount.text = "${playlist.count} $tracks"
            Glide.with(binding.root)
                .load(playlist.imagePath.ifEmpty { null })
                .placeholder(R.drawable.ic_placeholder_103)
                .centerCrop()
                .into(binding.playlistSmallCover)
        }
    }
}
