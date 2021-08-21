package com.auditionstreet.artist.ui.projects.repository

import com.auditionstreet.artist.model.response.MyProjectResponse
import com.silo.api.ApiService
import retrofit2.Response
import javax.inject.Inject

class MyProjectRepository @Inject constructor(val apiService: ApiService) {
    suspend fun getMyProjects(url: String):Response<MyProjectResponse> =apiService.getAllProjects(url)

}

