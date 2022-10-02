package dev.yashgarg.qbit.ui.server

import dev.yashgarg.qbit.Constants
import dev.yashgarg.qbit.FakeClientManager
import dev.yashgarg.qbit.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ServerViewModelTest {

    private lateinit var viewModel: ServerViewModel
    private val clientManager = FakeClientManager()

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        viewModel = ServerViewModel(clientManager)
    }

    @Test
    fun `check if mainData is loaded`() = runTest {
        viewModel.uiState.drop(1).first().apply {
            assertFalse(dataLoading)
            assertNotNull(data)
        }
    }

    @Test
    fun `check if torrent is added successfully`() = runTest {
        viewModel.addTorrentUrl(Constants.magnetUrl)

        assertEquals(viewModel.status.first(), "Successfully added torrent")
        viewModel.uiState.drop(1).first().apply {
            assertTrue(this.data!!.torrents.containsKey(Constants.magnetHash))
        }
    }
}
