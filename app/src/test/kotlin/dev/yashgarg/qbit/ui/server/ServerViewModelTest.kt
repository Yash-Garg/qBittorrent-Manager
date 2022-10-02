package dev.yashgarg.qbit.ui.server

import dev.yashgarg.qbit.FakeClientManager
import dev.yashgarg.qbit.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class ServerViewModelTest {

    private lateinit var viewModel: ServerViewModel
    private val clientManager = FakeClientManager()

    @get:Rule var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        viewModel = ServerViewModel(clientManager, mainCoroutineRule.testScope)
    }

    //    @Test
    //    fun `check if mainData is loaded`() {
    //        mainCoroutineRule.testScope.runTest {
    //            viewModel.uiState.drop(1).first().apply {
    //                assertFalse(dataLoading)
    //                assertTrue(data != null)
    //            }
    //        }
    //    }
}
