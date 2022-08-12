package dev.yashgarg.qbit.ui.torrent.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dev.yashgarg.qbit.ui.compose.Center
import dev.yashgarg.qbit.ui.compose.TorrentContentTreeView
import dev.yashgarg.qbit.ui.torrent.TorrentDetailsState
import dev.yashgarg.qbit.ui.torrent.TorrentDetailsViewModel

class TorrentFilesFragment : Fragment() {
    private val viewModel by
        viewModels<TorrentDetailsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext())
        composeView.apply {
            setContent {
                val state by viewModel.uiState.collectAsState()
                Mdc3Theme(setTextColors = true, setDefaultFontFamily = true) {
                    FilesListView(state)
                }
            }
        }

        return composeView
    }
}

@Composable
fun FilesListView(state: TorrentDetailsState) {
    if (state.contentTree.isNotEmpty()) {
        TorrentContentTreeView(state.contentTree)
    } else {
        Center { Text("No content found") }
    }
}
