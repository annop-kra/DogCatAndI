package com.example.dogcatandi.data.mapper

import android.util.Log
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class IsoDate

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class FlexiblePostcode

object DateAdapter {
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    private val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

    @FromJson
    @IsoDate
    fun fromJson(dateString: String?): String {
        Log.d("DateAdapter", "Input date: $dateString")
        return try {
            dateString?.takeIf { it.isNotBlank() }?.let {
                val date = isoDateFormat.parse(it)
                val formatted = outputFormat.format(date ?: Date())
                Log.d("DateAdapter", "Formatted date: $formatted")
                formatted
            } ?: ""
        } catch (e: Exception) {
            Log.e("DateAdapter", "Error parsing date: ${e.message}")
            ""
        }
    }

    @ToJson
    fun toJson(@IsoDate date: String): String {
        Log.d("DateAdapter", "ToJson date: $date")
        return date
    }
}

object PostcodeAdapter {
    @FromJson
    @FlexiblePostcode
    fun fromJson(value: Any?): String {
        Log.d("PostcodeAdapter", "Input postcode: $value")
        return when (value) {
            is Int -> value.toString()
            is String -> value
            is Double -> value.toInt().toString()
            else -> ""
        }.also { Log.d("PostcodeAdapter", "Output postcode: $it") }
    }

    @ToJson
    fun toJson(@FlexiblePostcode postcode: String): String {
        Log.d("PostcodeAdapter", "ToJson postcode: $postcode")
        return postcode
    }
}
