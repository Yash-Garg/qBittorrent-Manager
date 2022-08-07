package dev.yashgarg.qbit.ui.torrent.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.data.models.TrackerStatus
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
        val context = holder.itemView.context

        with(holder) {
            trackerUrl.text = tracker.url
            when (TrackerStatus.statusOf(tracker.status)) {
                TrackerStatus.DISABLED -> {
                    trackerUrl.setTextColor(context.getColor(R.color.red))
                }
                TrackerStatus.UPDATING -> {
                    trackerUrl.setTextColor(context.getColor(R.color.yellow))
                }
                TrackerStatus.NOT_CONTACTED -> {
                    trackerUrl.setTextColor(context.getColor(R.color.red))
                }
                TrackerStatus.CONTACTED_WORKING -> {
                    trackerUrl.setTextColor(context.getColor(R.color.green))
                }
                TrackerStatus.CONTACTED_NOT_WORKING -> {
                    trackerUrl.setTextColor(context.getColor(R.color.red))
                }
            }
        }
    }
}
