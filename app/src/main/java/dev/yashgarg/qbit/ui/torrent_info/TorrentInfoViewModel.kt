package dev.yashgarg.qbit.ui.torrent_info

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.di.ApplicationScope
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope

@HiltViewModel
class TorrentInfoViewModel
@Inject
constructor(
    private val clientManager: ClientManager,
    @ApplicationScope private val coroutineScope: CoroutineScope,
) : ViewModel() {}
