package dev.nonoxy.d2buildhelper.core.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.mp.KoinPlatform

fun initKoin(appDeclaration: KoinAppDeclaration? = null) {
    if (KoinPlatform.getKoinOrNull() != null) return
    startKoin {
        appDeclaration?.invoke(this)
        modules(appModule)
    }
}
