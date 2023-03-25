package dev.yashgarg.qbit.ui.torrent.tabs

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.common.R as CommonR
import dev.yashgarg.qbit.databinding.TorrentPeersFragmentBinding
import dev.yashgarg.qbit.ui.compose.Center
import dev.yashgarg.qbit.ui.compose.CenterLinearLoading
import dev.yashgarg.qbit.ui.compose.ListTile
import dev.yashgarg.qbit.ui.compose.theme.AppTypography
import dev.yashgarg.qbit.ui.compose.theme.bodyMediumPrimary
import dev.yashgarg.qbit.ui.torrent.TorrentDetailsState
import dev.yashgarg.qbit.ui.torrent.TorrentDetailsViewModel
import dev.yashgarg.qbit.utils.ClipboardUtil
import dev.yashgarg.qbit.utils.CountryFlags
import dev.yashgarg.qbit.utils.viewBinding
import qbittorrent.models.TorrentPeer

class TorrentPeersFragment : Fragment(R.layout.torrent_peers_fragment) {
    private val binding by viewBinding(TorrentPeersFragmentBinding::bind)
    private val viewModel by
        viewModels<TorrentDetailsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @OptIn(ExperimentalComposeUiApi::class)
        binding.peersComposeView.setContent {
            val state by viewModel.uiState.collectAsState()
            val scrollState = rememberNestedScrollInteropConnection()

            Mdc3Theme(
                setTextColors = true,
                readTypography = true,
                setDefaultFontFamily = true,
                readShapes = true,
                readColorScheme = true
            ) {
                PeersListView(
                    state,
                    Modifier.nestedScroll(scrollState),
                    onBan = { viewModel.banPeer(it) }
                )
            }
        }
    }
}

@Composable
fun PeersListView(
    state: TorrentDetailsState,
    modifier: Modifier = Modifier,
    onBan: (TorrentPeer) -> Unit
) {
    if (state.peersLoading) {
        CenterLinearLoading(modifier, R.color.md_theme_dark_seed)
    } else if (state.peers == null || state.peers.peers.isEmpty()) {
        Center(modifier) { Text("No peers connected") }
    } else {
        val peers = requireNotNull(state.peers).peers.values.toList()
        LazyColumn(modifier) {
            itemsIndexed(peers, key = { pos, peer -> "${peer.ip}-$pos" }) { _, peer ->
                val openDialog = remember { mutableStateOf(false) }
                val context = LocalContext.current

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
                    onClick = { openDialog.value = true },
                    onLongClick = {
                        ClipboardUtil.copyToClipboard(
                            context,
                            "peer_${peer.ip}",
                            "${peer.ip}:${peer.port}"
                        )
                    }
                )

                if (openDialog.value) {
                    AlertDialog(
                        tonalElevation = 5.dp,
                        onDismissRequest = { openDialog.value = false },
                        title = { Text(text = "Peer details", style = AppTypography.titleLarge) },
                        text = {
                            Text(
                                text =
                                    String.format(
                                        stringResource(CommonR.string.peer_details),
                                        peer.ip,
                                        peer.port,
                                        peer.country
                                    ),
                                style =
                                    AppTypography.bodyMedium.copy(
                                        lineHeight = 25.sp,
                                        fontSize = 15.sp
                                    ),
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        confirmButton = {
                            TextButton(onClick = { onBan(peer) }) {
                                Text("Ban Peer", style = bodyMediumPrimary)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { openDialog.value = false }) {
                                Text("Dismiss", style = bodyMediumPrimary)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
