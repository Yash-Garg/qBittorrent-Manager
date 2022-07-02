package dev.yashgarg.qbit.ui.server

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.utils.toHumanReadable
import qbittorrent.models.Torrent

class TorrentListAdapter(private val torrents: Map<String, Torrent>) :
    RecyclerView.Adapter<TorrentListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.torrentTitle)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val peers: TextView = view.findViewById(R.id.peers_tv)
        val speed: TextView = view.findViewById(R.id.speed_tv)
        val downloaded: TextView = view.findViewById(R.id.downloaded_percent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.torrent_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val torrent = torrents.values.elementAt(position)
        val tstate = torrent.state
        val context = holder.itemView.context

        with(holder) {
            title.text = torrent.name
            speed.text =
                String.format(
                    context.getString(R.string.speed_status),
                    torrent.dlspeed.toHumanReadable(),
                    torrent.uploadSpeed.toHumanReadable(),
                )
            progressBar.progress = ((torrent.downloaded / torrent.size) * 100).toInt()
            //            downloaded.text = String.format(
            //                context.getString(R.string.percent_done),
            //                torrent.downloaded.toInt(),
            //                torrent.size.toInt(),
            //                12
            //            )
            peers.text =
                String.format(
                    context.getString(R.string.seed_status),
                    torrent.connectedSeeds,
                    torrent.seedsInSwarm,
                )
        }
    }

    override fun getItemCount(): Int = torrents.size
}
