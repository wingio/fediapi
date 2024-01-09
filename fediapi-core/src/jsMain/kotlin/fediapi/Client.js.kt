package fediapi

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public actual val DefaultDispatcher: CoroutineDispatcher = Dispatchers.Main