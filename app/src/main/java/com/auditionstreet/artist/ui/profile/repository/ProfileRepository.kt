package com.auditionstreet.artist.ui.profile.repository

import com.auditionstreet.artist.model.response.DeleteMediaResponse
import com.auditionstreet.artist.model.response.GetBodyTypeLanguageResponse
import com.auditionstreet.artist.model.response.ProfileResponse
import com.auditionstreet.artist.model.response.UploadMediaResponse
import com.silo.api.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap
import javax.inject.Inject

class ProfileRepository @Inject constructor(val apiService: ApiService) {
    suspend fun getProfile(url: String):Response<ProfileResponse> =apiService.getProfile(url)
    suspend fun deleteMedia(url: String):Response<DeleteMediaResponse> =apiService.deleteMedia(url)

    suspend fun uploadMedia(
        requestProfileUpdate: HashMap<String, RequestBody>,
        media: List<MultipartBody.Part?>,
        profileImageFile: MultipartBody.Part?,
        introVideoUpload: MultipartBody.Part?
    ): Response<UploadMediaResponse> = apiService.uploadMedia(requestProfileUpdate,media,profileImageFile,introVideoUpload)

    suspend fun getLanguageBodyType(url: String):Response<GetBodyTypeLanguageResponse> =apiService.getLanguageBodyType(url)

}

