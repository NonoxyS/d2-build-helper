package dev.nonoxy.d2buildhelper.common.mappers

interface Mapper<From, To> {
    fun map(item: From): To

    fun map(list: List<From>): List<To> = list.map(::map)
}
