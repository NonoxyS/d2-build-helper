package dev.nonoxy.d2buildhelper.common.resources.di

import dev.nonoxy.d2buildhelper.common.resources.IosStringConverter
import dev.nonoxy.d2buildhelper.common.resources.StringConverter
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal actual val platformResourcesModule: Module = module {

    factoryOf<StringConverter>(::IosStringConverter)
}