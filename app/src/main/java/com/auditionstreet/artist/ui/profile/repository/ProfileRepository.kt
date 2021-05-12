package com.auditionstreet.artist.ui.profile.repository

import com.auditionstreet.artist.model.response.ProfileResponse
import com.silo.api.ApiService
import retrofit2.Response
import javax.inject.Inject

class ProfileRepository @Inject constructor(val apiService: ApiService) {
    suspend fun getProfile(url: String):Response<ProfileResponse> =apiService.getProfile(url)

}

