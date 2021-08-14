package com.auditionstreet.artist.ui.home.repository

import com.auditionstreet.artist.model.response.AddGroupResponse
import com.auditionstreet.artist.model.response.MyProjectResponse
import com.auditionstreet.artist.model.response.ProjectResponse
import com.silo.api.ApiService
import com.silo.model.request.AcceptRejectProjectRequest
import com.silo.model.request.ProjectRequest
import com.silo.model.request.ReportCastingRequest
import retrofit2.Response
import javax.inject.Inject

class ProjectRepository @Inject constructor(val apiService: ApiService) {
    suspend fun getProjects(projectRequest: ProjectRequest):Response<ProjectResponse> =apiService.getProjects(projectRequest)
    suspend fun getMyProjects(url: String):Response<MyProjectResponse> =apiService.getMyProjects(url)
    suspend fun acceptRejectProject(acceptRejectProjectRequest: AcceptRejectProjectRequest):Response<AddGroupResponse> =apiService.acceptRejectProject(acceptRejectProjectRequest)
    suspend fun reportCasting(reportCastingRequest: ReportCastingRequest):Response<AddGroupResponse> =apiService.reportCasting(reportCastingRequest)

}

