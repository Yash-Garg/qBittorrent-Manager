package dev.yashgarg.qbit.ui.version

import dev.yashgarg.qbit.FakeClientManager
import dev.yashgarg.qbit.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VersionViewModelTest {

    private lateinit var viewModel: VersionViewModel
    private val clientManager = FakeClientManager()

    @get:Rule var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        viewModel = VersionViewModel(clientManager)
    }

    @Test
    fun `check if client versions are correct`() {
        mainCoroutineRule.testScope.runTest {
            viewModel.uiState.drop(1).first().apply {
                assertEquals(appVersion, "v4.4.5")
                assertEquals(apiVersion, "2.8.5")
            }
        }
    }
}
