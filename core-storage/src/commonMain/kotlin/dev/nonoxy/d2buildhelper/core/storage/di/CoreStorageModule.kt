package dev.nonoxy.d2buildhelper.core.storage.di

import dev.nonoxy.d2buildhelper.core.storage.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import org.koin.dsl.module

val coreStorageModule = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_BASE_URL,
            supabaseKey = BuildConfig.SUPABASE_API_KEY,
        ) {
            install(Storage)
        }
    }
}
