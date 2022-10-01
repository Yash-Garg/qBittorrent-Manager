package dev.yashgarg.qbit.ui.config

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.yashgarg.qbit.BuildConfig
import dev.yashgarg.qbit.data.daos.ConfigDao
import dev.yashgarg.qbit.data.models.ConnectionType
import dev.yashgarg.qbit.data.models.ServerConfig
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class ConfigViewModelTest {

    private val config =
        ServerConfig(
            0,
            "TestServer",
            BuildConfig.BASE_URL,
            443,
            "admin",
            BuildConfig.PASSWORD,
            ConnectionType.HTTPS,
        )

    @get:Rule var hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: ConfigViewModel
    @Inject lateinit var configDao: ConfigDao

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        hiltRule.inject()
        viewModel = ConfigViewModel(configDao)
    }

    @Test
    fun checkFormValidation() {
        runTest {
            viewModel.validateForm(
                config.serverName,
                config.baseUrl,
                config.port.toString(),
                config.connectionType.toString().lowercase(),
                config.username,
                config.password
            )

            assertTrue(
                viewModel.validationEvents.first() == ConfigViewModel.ValidationEvent.Success
            )
        }
    }

    @Test
    fun checkClientConnection() {
        runTest {
            val response =
                viewModel.testConfig(
                    "${config.connectionType}://${config.baseUrl}",
                    config.username,
                    config.password,
                )

            when (response) {
                is Ok -> {
                    viewModel.insert(
                        config.serverName,
                        config.baseUrl,
                        config.port.toString(),
                        config.connectionType.toString().lowercase(),
                        config.username,
                        config.password
                    )
                    assertEquals(response.value, "v4.4.5")
                }
                is Err -> throw response.error
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
