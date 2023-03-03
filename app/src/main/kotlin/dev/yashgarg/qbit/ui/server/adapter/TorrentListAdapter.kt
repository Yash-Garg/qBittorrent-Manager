package dev.yashgarg.qbit.ui.server.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.Selection
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.common.R as CommonR
import dev.yashgarg.qbit.utils.toHumanReadable
import dev.yashgarg.qbit.utils.toTime
import javax.inject.Inject
import qbittorrent.models.Torrent

class TorrentListAdapter @Inject constructor() :
    ListAdapter<Torrent, TorrentListAdapter.TorrentItemViewHolder>(TorrentComparator()) {

    private var selectionTracker: SelectionTracker<String>? = null
    var onItemClick: ((String) -> Unit)? = null

    fun makeSelectable(recyclerView: RecyclerView, onSelection: (Selection<String>) -> Unit) {
        selectionTracker =
            SelectionTracker.Builder(
                    "SelectableTorrentListAdapter",
                    recyclerView,
                    itemKeyProvider,
                    TorrentItemDetailsLookup(recyclerView),
                    StorageStrategy.createStringStorage()
                )
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                .build()
                .apply {
                    addObserver(
                        object : SelectionTracker.SelectionObserver<String>() {
                            override fun onSelectionChanged() {
                                super.onSelectionChanged()
                                onSelection(selection)
                            }
                        }
                    )
                }
    }

    inner class TorrentItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardView: MaterialCardView = view.findViewById(R.id.torrent_card)
        private val title: TextView = view.findViewById(R.id.torrentTitle)
        private val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        private val peers: TextView = view.findViewById(R.id.peers_tv)
        private val speed: TextView = view.findViewById(R.id.speed_tv)
        private val downloaded: TextView = view.findViewById(R.id.downloaded_percent)
        private val eta: TextView = view.findViewById(R.id.eta_tv)

        val itemDetails: ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {
                override fun getPosition(): Int = adapterPosition

                override fun getSelectionKey(): String = getItem(position).hash
            }

        fun bind(torrent: Torrent) {
            with(itemView) {
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

                cardView.apply {
                    selectionTracker?.let {
                        setOnClickListener { _ ->
                            if (!it.hasSelection()) onItemClick?.invoke(torrent.hash)
                        }

                        isChecked = it.isSelected(torrent.hash)
                    }
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
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TorrentItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.torrent_item, parent, false)

        return TorrentItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TorrentItemViewHolder, position: Int) {
        val torrent = currentList.elementAt(position)

        holder.bind(torrent)
    }

    override fun getItemCount(): Int = currentList.size

    private class TorrentComparator : DiffUtil.ItemCallback<Torrent>() {
        override fun areItemsTheSame(oldItem: Torrent, newItem: Torrent): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Torrent, newItem: Torrent): Boolean {
            return oldItem.hash == newItem.hash
        }
    }

    private class TorrentItemDetailsLookup(private val recyclerView: RecyclerView) :
        ItemDetailsLookup<String>() {
        override fun getItemDetails(event: MotionEvent): ItemDetails<String>? {
            val view = recyclerView.findChildViewUnder(event.x, event.y)

            return if (view != null) {
                return (recyclerView.getChildViewHolder(view)
                        as TorrentListAdapter.TorrentItemViewHolder)
                    .itemDetails
            } else null
        }
    }

    private val itemKeyProvider =
        object : ItemKeyProvider<String>(SCOPE_CACHED) {
            override fun getKey(position: Int) = getItem(position).hash

            override fun getPosition(key: String): Int {
                return currentList.indexOfFirst { it.hash == key }
            }
        }
}
