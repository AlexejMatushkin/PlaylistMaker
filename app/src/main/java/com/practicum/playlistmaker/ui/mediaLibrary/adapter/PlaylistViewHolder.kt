package com.practicum.playlistmaker.ui.mediaLibrary.adapter

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.playlist.model.Playlist

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val cover: ImageView = view.findViewById(R.id.playlistCover)
    private val name: TextView = view.findViewById(R.id.playlistName)
    private val count: TextView = view.findViewById(R.id.playlistTrackCount)

    fun bind(playlist: Playlist) {
        name.text = playlist.name
        val tracksCount = playlist.count
        count.text = itemView.context.resources.getQuantityString(
            R.plurals.track_count,
            tracksCount,
            tracksCount
        )
        Glide.with(itemView)
            .load(playlist.imagePath.ifEmpty { null })
            .placeholder(R.drawable.ic_placeholder_103)
            .error(R.drawable.ic_placeholder_103)
            .transform(CenterCrop(), RoundedCorners(dpToPx(2f, itemView.context)))
            .into(cover)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}
