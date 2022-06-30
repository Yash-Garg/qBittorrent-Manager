package dev.yashgarg.qbit.ui.server

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.yashgarg.qbit.R
import qbittorrent.models.Torrent

class TorrentListAdapter(private val torrents: Map<String, Torrent>) :
    RecyclerView.Adapter<TorrentListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.torrentTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.torrent_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val torrent = torrents.values.elementAt(position)
        holder.title.text = torrent.name
    }

    override fun getItemCount(): Int = torrents.size
}
