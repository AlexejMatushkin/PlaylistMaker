package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(
    private var tracks: List<Track> = emptyList()
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    companion object {
        private const val CORNER_RADIUS_PX = 2f
    }

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
        private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
        private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)
        private val ivArtwork: ImageView = itemView.findViewById(R.id.ivArtwork)

        fun bind(track: Track) {
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvTrackTime.text = track.getFormattedTime()

            val cornerRadius = DimensionUtils.pxToDp(
                CORNER_RADIUS_PX,
                itemView.context
            )

            if (!track.artworkUrl100.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(track.artworkUrl100)
                    .placeholder(R.drawable.ic_music_note)
                    .error(R.drawable.ic_placeholder_error_track)
                    .centerCrop()
                    .transform(RoundedCorners(cornerRadius))
                    .into(ivArtwork)
            } else {
                ivArtwork.setImageResource(R.drawable.ic_music_note)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}
