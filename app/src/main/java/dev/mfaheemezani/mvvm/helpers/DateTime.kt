package dev.mfaheemezani.mvvm.helpers

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

object DateTime {

    val dateTimeServerFormat: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
    val dateTimeConvertedFormat: DateTimeFormatter = DateTimeFormat.forPattern("EEEE, MMMM dd, yyyy (HH:mm aaa)")

    fun convertToDateTimeHumanReadableFormat(dateTime: String) : String {
        return DateTime.parse(dateTime, dateTimeServerFormat).toString(dateTimeConvertedFormat)
    }
}