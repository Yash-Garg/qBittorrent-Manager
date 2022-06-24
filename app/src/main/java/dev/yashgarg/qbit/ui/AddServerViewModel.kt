package dev.yashgarg.qbit.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.yashgarg.qbit.database.AppDatabase
import javax.inject.Inject

@HiltViewModel
class AddServerViewModel @Inject constructor(private val db: AppDatabase) : ViewModel() {}
