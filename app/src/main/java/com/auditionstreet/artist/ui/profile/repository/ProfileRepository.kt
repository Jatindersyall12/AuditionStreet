package com.auditionstreet.artist.ui.profile.repository

import com.auditionstreet.artist.model.response.ProfileResponse
import com.auditionstreet.artist.model.response.UploadMediaResponse
import com.silo.api.ApiService
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class ProfileRepository @Inject constructor(val apiService: ApiService) {
    suspend fun getProfile(url: String):Response<ProfileResponse> =apiService.getProfile(url)
    suspend fun uploadMedia(media: List<MultipartBody.Part?>): Response<UploadMediaResponse> = apiService.uploadMedia(media)

}

