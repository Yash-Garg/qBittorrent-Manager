package dev.yashgarg.qbit.ui.torrent.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.yashgarg.qbit.ui.torrent.TorrentPeersFragment

class TorrentInfoAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment =
        when (position) {
            2 -> TorrentPeersFragment()
            else -> Fragment()
        }
}
