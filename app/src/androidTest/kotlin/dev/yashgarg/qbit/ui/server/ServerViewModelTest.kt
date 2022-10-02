package dev.yashgarg.qbit.ui.server

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.yashgarg.qbit.data.manager.ClientManager
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class ServerViewModelTest {

    @get:Rule var hiltRule = HiltAndroidRule(this)

    private val torrentUrl =
        "magnet:?xt=urn:btih:7cb890a8886ae03491ba5f706a5b6655963b8f01&dn=archlinux-2022.10.01-x86_64.iso"
    private lateinit var viewModel: ServerViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Inject lateinit var clientManager: ClientManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        hiltRule.inject()
        viewModel = ServerViewModel(clientManager, CoroutineScope(testDispatcher))
    }

    @Test
    fun verifyDataLoadedState() {
        runTest {
            delay(1000L)
            viewModel.uiState.drop(1).first().apply {
                assertFalse(dataLoading)
                assertTrue(data != null)
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
