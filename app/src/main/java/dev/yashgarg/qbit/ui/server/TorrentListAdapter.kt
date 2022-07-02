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
        val eta: TextView = view.findViewById(R.id.eta_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.torrent_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val torrent = torrents.values.elementAt(position)
        val tstate: Torrent.State = torrent.state
        val context = holder.itemView.context

        with(holder) {
            title.text = torrent.name
            speed.text =
                String.format(
                    context.getString(R.string.speed_status),
                    torrent.dlspeed.toHumanReadable(),
                    torrent.uploadSpeed.toHumanReadable(),
                )
            progressBar.progress = (torrent.progress * 100).toInt()
            downloaded.text =
                String.format(
                    context.getString(R.string.percent_done),
                    0,
                    torrent.size.toHumanReadable(),
                    (torrent.progress * 100).toInt(),
                )

            when (tstate) {
                Torrent.State.PAUSED_DL -> {
                    peers.text = context.getString(R.string.paused)
                    peers.setTextColor(context.getColor(R.color.yellowish))
                    speed.visibility = View.GONE
                }
                Torrent.State.UPLOADING -> {
                    peers.text = context.getString(R.string.seeding)
                }
                Torrent.State.DOWNLOADING -> {
                    eta.text = torrent.eta.toString()
                    peers.text =
                        String.format(
                            context.getString(R.string.seed_status),
                            torrent.connectedSeeds,
                            torrent.seedsInSwarm,
                        )
                    peers.setTextColor(context.getColor(R.color.accent))
                }
                else -> {}
            }
        }
    }

    override fun getItemCount(): Int = torrents.size
}
