package dev.yashgarg.qbit.ui.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.database.AppDatabase
import dev.yashgarg.qbit.models.ServerConfig
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class ConfigViewModel @Inject constructor(private val db: AppDatabase) : ViewModel() {
    fun insert(config: ServerConfig) {
        viewModelScope.launch { db.configDao().addConfig(config) }
    }
}
