package se.ox.assigment.network.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import se.ox.assigment.network.api.ApiResponse

interface ApiService {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int = 1
    ): Response<ApiResponse>
}