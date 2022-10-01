package dev.yashgarg.qbit.ui.version

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.yashgarg.qbit.data.manager.ClientManager
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
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
class VersionViewModelTest {

    @get:Rule var hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: VersionViewModel

    @Inject lateinit var clientManager: ClientManager

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        hiltRule.inject()
        viewModel = VersionViewModel(clientManager)
    }

    @Test
    fun checkVersionsFromClient() {
        runTest {
            viewModel.uiState.drop(1).first().apply {
                assertEquals(appVersion, "v4.4.5")
                assertEquals(apiVersion, "2.8.5")
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
