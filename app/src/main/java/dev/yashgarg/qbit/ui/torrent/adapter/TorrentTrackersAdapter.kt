package dev.yashgarg.qbit.ui.torrent.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.yashgarg.qbit.R
import javax.inject.Inject
import qbittorrent.models.TorrentTracker

class TorrentTrackersAdapter @Inject constructor() :
    RecyclerView.Adapter<TorrentTrackersAdapter.ViewHolder>() {

    private var trackerList = emptyList<TorrentTracker>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val trackerUrl: TextView = view.findViewById(R.id.trackerName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tracker_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = trackerList.size
    @SuppressLint("NotifyDataSetChanged")
    fun updateTrackers(trackers: List<TorrentTracker>) {
        trackerList = trackers
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tracker = trackerList.elementAt(position)

        with(holder) {
            trackerUrl.text = tracker.url
            // TODO: Set tracker status indicator according to the status values
            // https://github.com/qbittorrent/qBittorrent/wiki/WebUI-API-(qBittorrent-4.1)#get-torrent-trackers
        }
    }
}
