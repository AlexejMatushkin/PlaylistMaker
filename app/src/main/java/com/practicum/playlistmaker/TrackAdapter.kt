package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackAdapter(
    private val data: List<Track>
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
        private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
        private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)
        private val ivArtwork: ImageView = itemView.findViewById(R.id.ivArtwork)

        fun bind(track: Track) {
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvTrackTime.text = track.trackTime

            val cornerRadius = pxToDp(2f, itemView.context)

            Glide.with(itemView.context)
                .load(track.artworkUrl100)
                .placeholder(R.drawable.ic_music_note)
                .centerCrop()
                .transform(RoundedCorners(cornerRadius))
                .into(ivArtwork)
        }

        private fun pxToDp(px: Float, context: Context): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                px,
                context.resources.displayMetrics
            ).toInt()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

}
