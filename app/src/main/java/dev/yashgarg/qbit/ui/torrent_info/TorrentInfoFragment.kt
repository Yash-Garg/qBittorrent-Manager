package dev.yashgarg.qbit.ui.torrent_info

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.TorrentInfoFragmentBinding
import dev.yashgarg.qbit.utils.viewBinding

@AndroidEntryPoint
class TorrentInfoFragment : Fragment(R.layout.torrent_info_fragment) {
    private val binding by viewBinding(TorrentInfoFragmentBinding::bind)
    private val viewModel by viewModels<TorrentInfoViewModel>()
    private val args: TorrentInfoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionbar()
        binding.hash.text = args.torrentHash
    }

    private fun setupActionbar() {
        binding.toolbar.setNavigationOnClickListener { it.findNavController().navigateUp() }
    }
}
