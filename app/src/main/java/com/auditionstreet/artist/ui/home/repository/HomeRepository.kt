package com.auditionstreet.artist.ui.home.repository

import com.auditionstreet.artist.model.response.HomeApiResponse
import com.silo.api.ApiService
import com.silo.model.request.LoginRequest
import com.silo.model.request.ProjectRequest
import com.silo.model.response.LoginResponse
import retrofit2.Response
import javax.inject.Inject

class HomeRepository @Inject constructor(val apiService: ApiService) {
    suspend fun getHomeScreenData(url: String):Response<HomeApiResponse> =
        apiService.getHomeData(url)
}

