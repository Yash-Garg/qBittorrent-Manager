package dev.yashgarg.qbit.ui.config

import dev.yashgarg.qbit.data.daos.ConfigDao
import dev.yashgarg.qbit.data.models.ConnectionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class ConfigViewModelTest {
    private lateinit var viewModel: ConfigViewModel

    @Mock private lateinit var cfgDao: ConfigDao

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = ConfigViewModel(cfgDao)
    }

    @Test
    fun `check if form is valid by passing the details`() {
        runTest {
            viewModel.validateForm(
                "TestServer",
                "127.0.0.1",
                "55455",
                ConnectionType.HTTP.toString().lowercase(),
                "yash",
                "adminadmin"
            )

            assertTrue(
                viewModel.validationEvents.first() == ConfigViewModel.ValidationEvent.Success
            )
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
