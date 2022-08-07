package dev.yashgarg.qbit.ui.torrent.tabs

import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.TorrentFilesFragmentBinding
import dev.yashgarg.qbit.ui.compose.Center
import dev.yashgarg.qbit.ui.torrent.TorrentDetailsState
import dev.yashgarg.qbit.ui.torrent.TorrentDetailsViewModel
import dev.yashgarg.qbit.utils.viewBinding

class TorrentFilesFragment : Fragment(R.layout.torrent_files_fragment) {
    private val binding by viewBinding(TorrentFilesFragmentBinding::bind)
    private val viewModel by
        viewModels<TorrentDetailsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.filesComposeView.setContent {
            val state by viewModel.uiState.collectAsState()
            Mdc3Theme(setTextColors = true, setDefaultFontFamily = true) { FilesListView(state) }
        }
    }
}

@Composable
fun FilesListView(state: TorrentDetailsState) {
    if (state.torrentFiles.isNotEmpty()) {
        LazyColumn {
            itemsIndexed(state.torrentFiles) { _, file ->
                val pathSplit = file.name.split("/")
                val fileName = pathSplit.subList(1, pathSplit.size).joinToString("/")
                Text(
                    fileName,
                    modifier = Modifier.padding(8.dp),
                )
            }
        }
    } else {
        Center { Text("No content found") }
    }
}
