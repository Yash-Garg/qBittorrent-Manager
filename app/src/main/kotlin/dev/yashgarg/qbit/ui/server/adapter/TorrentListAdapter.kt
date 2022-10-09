package dev.yashgarg.qbit.ui.server.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.common.R as CommonR
import dev.yashgarg.qbit.utils.toHumanReadable
import dev.yashgarg.qbit.utils.toTime
import javax.inject.Inject
import qbittorrent.models.Torrent

class TorrentListAdapter @Inject constructor() :
    RecyclerView.Adapter<TorrentListAdapter.ViewHolder>() {

    var torrentsList = emptyMap<String, Torrent>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    var onItemClick: ((String) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.torrent_card)
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
        val torrent = torrentsList.values.elementAt(position)
        val context = holder.itemView.context

        with(holder) {
            title.text = torrent.name
            speed.text =
                String.format(
                    context.getString(CommonR.string.speed_status),
                    torrent.dlspeed.toHumanReadable(),
                    torrent.uploadSpeed.toHumanReadable(),
                )
            progressBar.progress = (torrent.progress * 100).toInt()
            downloaded.text =
                String.format(
                    context.getString(CommonR.string.percent_done),
                    torrent.downloaded.toLong().toHumanReadable(),
                    torrent.size.toHumanReadable(),
                    (torrent.progress * 100).toInt(),
                )
            eta.text = if (torrent.eta == 8640000L) null else torrent.eta.toTime()

            cardView.setOnClickListener {
                onItemClick?.invoke(torrentsList.keys.elementAt(position))
            }

            when (torrent.state) {
                Torrent.State.PAUSED_DL -> {
                    peers.text = context.getString(CommonR.string.paused)
                    peers.setTextColor(context.getColor(R.color.yellow))
                    speed.visibility = View.GONE
                    eta.visibility = View.GONE
                }
                Torrent.State.UPLOADING,
                Torrent.State.FORCED_UP -> {
                    peers.text = context.getString(CommonR.string.seeding)
                    peers.setTextColor(context.getColor(R.color.green))
                    speed.visibility = View.VISIBLE
                    eta.visibility = View.GONE
                }
                Torrent.State.DOWNLOADING,
                Torrent.State.FORCED_DL -> {
                    peers.text =
                        String.format(
                            context.getString(CommonR.string.seed_status),
                            torrent.connectedSeeds,
                            torrent.seedsInSwarm,
                        )
                    peers.setTextColor(context.getColor(R.color.md_theme_dark_seed))
                    speed.visibility = View.VISIBLE
                    eta.visibility = View.VISIBLE
                }
                Torrent.State.STALLED_DL,
                Torrent.State.STALLED_UP -> {
                    peers.text = context.getString(CommonR.string.stalled)
                    peers.setTextColor(context.getColor(R.color.red))
                    speed.visibility = View.GONE
                    eta.visibility = View.GONE
                }
                Torrent.State.PAUSED_UP -> {
                    peers.text = context.getString(CommonR.string.completed)
                    peers.setTextColor(context.getColor(R.color.md_theme_dark_seed))
                    speed.visibility = View.GONE
                    eta.visibility = View.GONE
                }
                // TODO: Add remaining changes to left-out branches
                Torrent.State.ERROR -> {}
                Torrent.State.MISSING_FILES -> {}
                Torrent.State.QUEUED_UP -> {}
                Torrent.State.CHECKING_UP -> {}
                Torrent.State.ALLOCATING -> {}
                Torrent.State.META_DL -> {}
                Torrent.State.CHECKING_DL -> {}
                Torrent.State.CHECKING_RESUME_DATA -> {}
                Torrent.State.MOVING -> {}
                Torrent.State.UNKNOWN -> {}
                Torrent.State.QUEUED_DL -> {}
                else -> throw IllegalArgumentException("Invalid torrent state received")
            }
        }
    }

    override fun getItemCount(): Int = torrentsList.size
}
