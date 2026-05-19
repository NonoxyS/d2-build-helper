package dev.nonoxy.d2buildhelper.common.utils

object TimeConverter {
    fun convertSecondsToMinutesAndSeconds(seconds: Int?): String {
        return if (seconds != null) {
            if (seconds > 0)
                "${if (seconds / 60 >= 10) seconds / 60 else "0${seconds / 60}"}:" +
                        "${if (seconds % 60 >= 10) seconds % 60 else "0${seconds % 60}"}"
            else "00:00"
        } else ""
    }
}