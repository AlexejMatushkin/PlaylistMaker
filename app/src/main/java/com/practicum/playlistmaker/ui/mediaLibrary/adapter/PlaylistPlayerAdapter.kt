package com.practicum.playlistmaker.ui.mediaLibrary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.playlist.model.Playlist

class PlaylistPlayerAdapter(
    private val playlists: List<Playlist>,
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistPlayerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistPlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.playlist_small_view, parent, false)
        return PlaylistPlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistPlayerViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener { onItemClick(playlist) }
    }

    override fun getItemCount(): Int = playlists.size
}
