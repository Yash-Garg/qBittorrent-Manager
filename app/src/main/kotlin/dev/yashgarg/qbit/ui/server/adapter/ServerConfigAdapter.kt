package dev.yashgarg.qbit.ui.server.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.data.models.ServerConfig
import dev.yashgarg.qbit.ui.common.ListTileTextView
import javax.inject.Inject

class ServerConfigAdapter @Inject constructor() :
    RecyclerView.Adapter<ServerConfigAdapter.ViewHolder>() {

    var configs = emptyList<ServerConfig>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            notifyDataSetChanged()
            field = value
        }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serverTitle: ListTileTextView = view.findViewById(R.id.server_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.config_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val config = configs[position]

        with(holder) {
            serverTitle.apply {
                title = config.serverName
                subtitle = "${config.baseUrl}:${config.port}"
            }
        }
    }

    override fun getItemCount() = configs.size
}
