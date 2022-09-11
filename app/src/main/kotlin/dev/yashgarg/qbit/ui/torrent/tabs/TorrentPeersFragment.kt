package dev.yashgarg.qbit.ui.torrent.tabs

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.TorrentPeersFragmentBinding
import dev.yashgarg.qbit.ui.compose.Center
import dev.yashgarg.qbit.ui.compose.ListTile
import dev.yashgarg.qbit.ui.torrent.TorrentDetailsState
import dev.yashgarg.qbit.ui.torrent.TorrentDetailsViewModel
import dev.yashgarg.qbit.utils.CountryFlags
import dev.yashgarg.qbit.utils.viewBinding

class TorrentPeersFragment : Fragment(R.layout.torrent_peers_fragment) {
    private val binding by viewBinding(TorrentPeersFragmentBinding::bind)
    private val viewModel by
        viewModels<TorrentDetailsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.peersComposeView.setContent {
            val state by viewModel.uiState.collectAsState()
            Mdc3Theme(setTextColors = true, readTypography = true, setDefaultFontFamily = true) {
                PeersListView(state)
            }
        }
    }
}

@Composable
fun PeersListView(state: TorrentDetailsState) {
    if (state.peersLoading || state.peers == null) {
        Center { LinearProgressIndicator(color = colorResource(R.color.md_theme_dark_seed)) }
    } else if (state.peers.peers.isEmpty()) {
        Center { Text("No peers connected") }
    } else {
        val peers = requireNotNull(state.peers).peers.values.toList()
        LazyColumn {
            itemsIndexed(peers) { _, peer ->
                ListTile(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    title = "${peer.ip}:${peer.port}",
                    subtitle = "Connection: ${peer.connection}",
                    suffix = {
                        Text(
                            CountryFlags.getCountryFlagByCountryCode(peer.countryCode),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    onClick = {}
                )
            }
        }
    }
}
