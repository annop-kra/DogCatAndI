package com.example.dogcati.data.mapper

import com.squareup.moshi.Moshi
import junit.framework.TestCase.assertEquals
import org.junit.Test

class MoshiAdaptersTest {

    private val moshi = Moshi.Builder()
        .add(DateAdapter)
        .add(PostcodeAdapter)
        .build()

    @Test
    fun `test DateAdapter with valid ISO date`() {
        val json = """{"date":"1968-01-27T01:27:31.087Z"}"""
        val adapter = moshi.adapter(TestDate::class.java)
        val result = adapter.fromJson(json)
        assertEquals("27/01/1968", result?.date)
    }

    @Test
    fun `test DateAdapter with null date`() {
        val json = """{"date":null}"""
        val adapter = moshi.adapter(TestDate::class.java)
        val result = adapter.fromJson(json)
        assertEquals("", result?.date)
    }

    @Test
    fun `test DateAdapter with invalid date`() {
        val json = """{"date":"invalid-date"}"""
        val adapter = moshi.adapter(TestDate::class.java)
        val result = adapter.fromJson(json)
        assertEquals("", result?.date)
    }

    @Test
    fun `test PostcodeAdapter with integer postcode`() {
        val json = """{"postcode":13002}"""
        val adapter = moshi.adapter(TestPostcode::class.java)
        val result = adapter.fromJson(json)
        assertEquals("13002", result?.postcode)
    }

    @Test
    fun `test PostcodeAdapter with string postcode`() {
        val json = """{"postcode":"13002"}"""
        val adapter = moshi.adapter(TestPostcode::class.java)
        val result = adapter.fromJson(json)
        assertEquals("13002", result?.postcode)
    }

    @Test
    fun `test PostcodeAdapter with null postcode`() {
        val json = """{"postcode":null}"""
        val adapter = moshi.adapter(TestPostcode::class.java)
        val result = adapter.fromJson(json)
        assertEquals("", result?.postcode)
    }
}

data class TestDate(
    @IsoDate val date: String?
)

data class TestPostcode(
    @FlexiblePostcode val postcode: String?
)