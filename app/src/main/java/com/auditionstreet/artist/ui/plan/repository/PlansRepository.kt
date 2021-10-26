package com.auditionstreet.artist.ui.plan.repository

import com.auditionstreet.artist.model.response.AddGroupResponse
import com.auditionstreet.artist.model.response.DeleteMediaResponse
import com.auditionstreet.artist.model.response.MyProjectResponse
import com.auditionstreet.artist.model.response.PlansListResponse
import com.silo.api.ApiService
import com.silo.model.request.AcceptRejectProjectRequest
import com.silo.model.request.PurchasePlanRequest
import retrofit2.Response
import javax.inject.Inject

class PlansRepository @Inject constructor(val apiService: ApiService) {
    suspend fun getPlansList(url: String):Response<PlansListResponse> =apiService.getPlansList(url)
    suspend fun acceptRejectProject(purchasePlanRequest: PurchasePlanRequest):
            Response<DeleteMediaResponse> =apiService.purchasePlan(purchasePlanRequest)


}

