package com.example.cft.data.api

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import okhttp3.Request

object RandomApi {

    fun get(): JsonArray {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://randomuser.me/api/")
            .get()
            .build()
        val jsonData = client.newCall(request).execute().body!!.string()
        return JsonParser.parseString(jsonData).asJsonObject.get("results").asJsonArray
    }
}