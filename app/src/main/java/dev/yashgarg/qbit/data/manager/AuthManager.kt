package dev.yashgarg.qbit.data.manager

import dagger.hilt.android.qualifiers.ApplicationContext
import dev.yashgarg.qbit.data.daos.ConfigDao
import javax.inject.Inject

class AuthManager
@Inject
constructor(
    @ApplicationContext private val applicationContext: ApplicationContext,
    private val configDao: ConfigDao,
) {}
