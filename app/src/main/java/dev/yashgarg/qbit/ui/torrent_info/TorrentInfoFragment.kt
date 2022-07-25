package dev.yashgarg.qbit.ui.torrent_info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.TorrentInfoFragmentBinding
import dev.yashgarg.qbit.utils.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TorrentInfoFragment : Fragment(R.layout.torrent_info_fragment) {
    private val binding by viewBinding(TorrentInfoFragmentBinding::bind)
    private val viewModel by viewModels<TorrentInfoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialElevationScale(false)
        reenterTransition = MaterialElevationScale(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeFlows()
    }

    private fun observeFlows() {
        viewModel.uiState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun render(state: TorrentInfoState) {
        with(binding) {
            toolbar.setNavigationOnClickListener { it.findNavController().navigateUp() }
            if (!state.loading) {
                val torrent = requireNotNull(state.torrent)
                hash.text = viewModel.hash
                toolbar.title = torrent.name
            }
        }
    }
}
