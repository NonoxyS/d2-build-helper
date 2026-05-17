package extensions

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider

internal inline fun <reified T : Any> ObjectFactory.property(defaultValue: T?): Property<T> {
    return property(T::class.java).convention(defaultValue)
}

internal inline fun <reified T : Any> ObjectFactory.property(defaultValue: Provider<T>): Property<T> {
    return property(T::class.java).convention(defaultValue)
}
