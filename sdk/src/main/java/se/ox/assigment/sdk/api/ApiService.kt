package se.ox.assigment.sdk.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1
    ): Response<ApiResponse>
}