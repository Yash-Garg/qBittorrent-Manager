package dev.yashgarg.qbit.data

import com.github.michaelbull.result.Ok
import dev.yashgarg.qbit.Constants
import dev.yashgarg.qbit.FakeClientManager
import dev.yashgarg.qbit.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QbitRepositoryTest {
    private lateinit var repository: QbitRepository
    private val clientManager = FakeClientManager()

    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = QbitRepository(Dispatchers.Main, clientManager)
    }

    @Test
    fun checkClientConnected() = runTest {
        assertTrue(repository.getVersion() is Ok)
        assertTrue(repository.getApiVersion() is Ok)
    }

    @Test
    fun checkAddTorrentSuccess() = runTest {
        assertTrue(repository.addTorrentUrl(Constants.magnetUrl) is Ok)

        val data = repository.observeMainData().first()
        assertTrue(data.torrents.containsKey(Constants.magnetHash))
    }

    @Test
    fun checkRemoveTorrentSuccess() = runTest {
        assertTrue(repository.removeTorrents(listOf(Constants.magnetHash)) is Ok)

        val data = repository.observeMainData().first()
        assertFalse(data.torrents.containsKey(Constants.magnetHash))
    }
}
