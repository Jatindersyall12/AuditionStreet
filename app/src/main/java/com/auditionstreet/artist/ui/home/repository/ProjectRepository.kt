package com.auditionstreet.artist.ui.home.repository

import com.auditionstreet.artist.model.response.ProjectResponse
import com.silo.api.ApiService
import com.silo.model.request.ProjectRequest
import retrofit2.Response
import javax.inject.Inject

class ProjectRepository @Inject constructor(val apiService: ApiService) {
    suspend fun getProjects(projectRequest: ProjectRequest):Response<ProjectResponse> =apiService.getProjects(projectRequest)

}

