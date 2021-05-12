package com.auditionstreet.artist.ui.projects.repository

import com.auditionstreet.artist.model.response.MyProjectDetailResponse
import com.silo.api.ApiService
import retrofit2.Response
import javax.inject.Inject

class MyProjectDetailRepository @Inject constructor(val apiService: ApiService) {
   suspend fun getMyProjectDetail(url: String):Response<MyProjectDetailResponse> =apiService.getMyProjectDetail(url)

}
