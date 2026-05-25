package dev.nonoxy.d2buildhelper.common.di

import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchers
import dev.nonoxy.d2buildhelper.common.coroutines.CoroutineDispatchersImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {
    singleOf(::CoroutineDispatchersImpl) { bind<CoroutineDispatchers>() }
}
