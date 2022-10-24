package dev.yashgarg.qbit.ui.server

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.MainActivity
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.ServerFragmentBinding
import dev.yashgarg.qbit.ui.dialogs.AddTorrentDialog
import dev.yashgarg.qbit.ui.server.adapter.TorrentListAdapter
import dev.yashgarg.qbit.utils.viewBinding
import java.util.ArrayList
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ServerFragment : Fragment(R.layout.server_fragment) {
    private val binding by viewBinding(ServerFragmentBinding::bind)
    private val viewModel by viewModels<ServerViewModel>()

    @Inject lateinit var torrentListAdapter: TorrentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHandlers()
        observeFlows()
        setupDialogResultListener()
    }

    override fun onStop() {
        super.onStop()
        binding.refreshLayout.isEnabled = false
        binding.torrentRv.adapter = null
    }

    override fun onResume() {
        super.onResume()
        binding.refreshLayout.isEnabled = true
        binding.torrentRv.adapter = torrentListAdapter
    }

    private fun setupDialogResultListener() {
        childFragmentManager.setFragmentResultListener(
            AddTorrentDialog.ADD_TORRENT_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val url = bundle.getString(AddTorrentDialog.TORRENT_KEY)
            viewModel.addTorrentUrl(requireNotNull(url))
        }

        @Suppress("UNCHECKED_CAST")
        childFragmentManager.setFragmentResultListener(
            AddTorrentDialog.ADD_TORRENT_FILE_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val uris =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelableArrayList(AddTorrentDialog.TORRENT_KEY, Uri::class.java)
                } else {
                    bundle.getStringArrayList(AddTorrentDialog.TORRENT_KEY) as ArrayList<Uri>
                }

            uris?.forEach { uri ->
                requireContext().contentResolver.openInputStream(uri).use { stream ->
                    viewModel.addTorrentFile(requireNotNull(stream).readBytes())
                }
            }
        }
    }

    private fun handleAddIntent() {
        val uri: String? = arguments?.getString(MainActivity.TORRENT_INTENT_KEY)
        arguments?.clear()
        if (!uri.isNullOrEmpty()) {
            if (
                uri.startsWith("http://") ||
                    uri.startsWith("https://") ||
                    uri.startsWith("magnet:?xt=urn:")
            ) {
                viewModel.addTorrentUrl(uri)
            } else if (uri.startsWith("content://") || uri.startsWith("file://")) {
                requireContext().contentResolver.openInputStream(Uri.parse(uri)).use { stream ->
                    viewModel.addTorrentFile(requireNotNull(stream).readBytes())
                }
            }
        }
    }

    private fun setupHandlers() {
        with(binding) {
            torrentListAdapter.onItemClick = { hash ->
                val action =
                    ServerFragmentDirections.actionServerFragmentToTorrentInfoFragment(hash)
                findNavController().navigate(action)
            }

            torrentRv.adapter = torrentListAdapter

            refreshLayout.setOnRefreshListener { viewModel.refresh() }

            addTorrentFab.setOnClickListener {
                AddTorrentDialog.newInstance().show(childFragmentManager, AddTorrentDialog.TAG)
            }

            bottomBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.category -> {
                        true
                    }
                    R.id.sort_list -> {
                        true
                    }
                    R.id.speed_toggle -> {
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun observeFlows() {
        viewModel.uiState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.status
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.intent
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { handleAddIntent() }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun render(state: ServerState) {
        with(binding) {
            if (state.hasError) {
                listLoader.visibility = View.GONE
                errorTv.text =
                    state.error?.message
                        ?: requireContext().getString(dev.yashgarg.qbit.common.R.string.error)
                errorTv.visibility = View.VISIBLE
                torrentRv.visibility = View.GONE
                refreshLayout.isRefreshing = false
                emptyTv.visibility = View.GONE
            } else if (!state.dataLoading) {
                errorTv.visibility = View.GONE
                listLoader.visibility = View.GONE
                if (state.data?.torrents.isNullOrEmpty()) {
                    emptyTv.visibility = View.VISIBLE
                    torrentRv.visibility = View.GONE
                } else {
                    emptyTv.visibility = View.GONE
                    torrentRv.apply {
                        visibility = View.VISIBLE
                        torrentListAdapter.torrentsList = state.data!!.torrents
                    }
                }
                refreshLayout.isRefreshing = false
            }
        }
    }
}
