package com.demo.assignmentapplication.data.remote

import com.google.gson.JsonObject
import retrofit2.http.GET

interface ApiServices {

    @GET("/")
    suspend fun getHomepage(): List<JsonObject>
}