package dev.yashgarg.qbit.ui.torrent.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.yashgarg.qbit.ui.torrent.tabs.TorrentFilesFragment
import dev.yashgarg.qbit.ui.torrent.tabs.TorrentInfoFragment
import dev.yashgarg.qbit.ui.torrent.tabs.TorrentPeersFragment
import dev.yashgarg.qbit.ui.torrent.tabs.TorrentTrackersFragment

class TorrentDetailsAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> TorrentInfoFragment()
            1 -> TorrentFilesFragment()
            2 -> TorrentTrackersFragment()
            3 -> TorrentPeersFragment()
            else -> Fragment()
        }
}
