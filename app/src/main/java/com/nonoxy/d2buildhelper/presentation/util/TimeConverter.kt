package com.nonoxy.d2buildhelper.presentation.util

object TimeConverter {
    fun convertSecondsToMinutesAndSeconds(seconds: Int): String =
        if (seconds > 0)
            "${ if (seconds / 60 >= 10) seconds / 60 else "0${seconds / 60}" }:" +
                    "${ if (seconds % 60 >= 10) seconds % 60 else "0${seconds % 60}" }"
        else if (seconds == -404) ""
        else "00:00"
}