package com.auditionstreet.artist.ui.profile.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auditionstreet.artist.api.ApiConstant
import com.auditionstreet.artist.model.response.DeleteMediaResponse
import com.auditionstreet.artist.model.response.ProfileResponse
import com.auditionstreet.artist.model.response.UploadMediaResponse
import com.auditionstreet.artist.ui.profile.repository.ProfileRepository
import com.leo.wikireviews.utils.livedata.Event
import com.silo.model.request.WorkGalleryRequest
import com.silo.utils.network.NetworkHelper
import com.silo.utils.network.Resource
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.HashMap

class ProfileViewModel @ViewModelInject constructor(
    private val profileRepository: ProfileRepository,
    private val networkHelper: NetworkHelper,
) : ViewModel() {
    private val IMAGE_EXTENSION = "/*"

    private val profile = MutableLiveData<Event<Resource<ProfileResponse>>>()
    val getProfile: LiveData<Event<Resource<ProfileResponse>>>
        get() = profile

    private val delete = MutableLiveData<Event<Resource<DeleteMediaResponse>>>()
    val deleteMedia: LiveData<Event<Resource<DeleteMediaResponse>>>
        get() = delete

    private val upload_media = MutableLiveData<Event<Resource<UploadMediaResponse>>>()
    val uploadMedia: LiveData<Event<Resource<UploadMediaResponse>>>
        get() = upload_media


    fun getProfile(url: String) {
        viewModelScope.launch {
            profile.postValue(Event(Resource.loading(ApiConstant.GET_PROFILE, null)))
            if (networkHelper.isNetworkConnected()) {
                profileRepository.getProfile(url).let {
                    if (it.isSuccessful && it.body() != null) {
                        profile.postValue(
                            Event(
                                Resource.success(
                                    ApiConstant.GET_PROFILE,
                                    it.body()
                                )
                            )
                        )
                    } else {
                        profile.postValue(
                            Event(
                                Resource.error(
                                    ApiConstant.GET_PROFILE,
                                    it.code(),
                                    it.errorBody().toString(),
                                    null
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    fun deleteMedia(url: String) {
        viewModelScope.launch {
            delete.postValue(Event(Resource.loading(ApiConstant.DELETE_MEDIA, null)))
            if (networkHelper.isNetworkConnected()) {
                profileRepository.deleteMedia(url).let {
                    if (it.isSuccessful && it.body() != null) {
                        delete.postValue(
                            Event(
                                Resource.success(
                                    ApiConstant.DELETE_MEDIA,
                                    it.body()
                                )
                            )
                        )
                    } else {
                        delete.postValue(
                            Event(
                                Resource.error(
                                    ApiConstant.DELETE_MEDIA,
                                    it.code(),
                                    it.errorBody().toString(),
                                    null
                                )
                            )
                        )
                    }
                }
            }
        }
    }


    fun uploadMedia(
        list: ArrayList<WorkGalleryRequest>,
        profileImageFile: File?,
        selectedProfileImage: String,
        introVideoPath: String,
        requestProfileUpdate: HashMap<String, RequestBody>
    ) {
        viewModelScope.launch {
            upload_media.postValue(Event(Resource.loading(ApiConstant.UPLOAD_MEDIA, null)))
            var profileImageUpload: MultipartBody.Part? = null
            var introVideoUpload: MultipartBody.Part? = null
            var imageOrVideoUpload: MultipartBody.Part

            val mediaList = ArrayList<MultipartBody.Part>()
            if (networkHelper.isNetworkConnected()) {
                if (!introVideoPath.isEmpty()) {
                    val introVideo = File(introVideoPath)
                    val fileIntroVideo =
                        RequestBody.create(
                            IMAGE_EXTENSION.toMediaTypeOrNull(),
                            introVideo!!
                        )
                    introVideoUpload =
                        MultipartBody.Part.createFormData(
                            "video",
                            "IntroVideo",
                            fileIntroVideo
                        )
                }
                if (!selectedProfileImage.isEmpty()) {
                    val profileImage =
                        RequestBody.create(
                            IMAGE_EXTENSION.toMediaTypeOrNull(),
                            profileImageFile!!
                        )
                    profileImageUpload =
                        MultipartBody.Part.createFormData(
                            "image",
                            selectedProfileImage,
                            profileImage
                        )
                }

                for (i in 0 until list.size) {
                    val videoOrImage = File(list.get(i).path)
                    val profileImage =
                        RequestBody.create(
                            IMAGE_EXTENSION.toMediaTypeOrNull(),
                            videoOrImage!!
                        )
                    if (list.get(i).isImage) {
                        imageOrVideoUpload =
                            MultipartBody.Part.createFormData(
                                "media[]",
                                "Image",
                                profileImage
                            )
                    } else {
                        imageOrVideoUpload =
                            MultipartBody.Part.createFormData(
                                "media[]",
                                "Video",
                                profileImage
                            )
                    }
                    mediaList.add(imageOrVideoUpload)
                }
                profileRepository.uploadMedia(requestProfileUpdate,mediaList,profileImageUpload,introVideoUpload).let {
                    if (it.isSuccessful && it.body() != null) {
                        upload_media.postValue(
                            (Event(
                                Resource.success(
                                    ApiConstant.UPLOAD_MEDIA,
                                    it.body()
                                )
                            ))
                        )
                    } else {
                        upload_media.postValue(
                            Event(
                                Resource.error(
                                    ApiConstant.UPLOAD_MEDIA,
                                    it.code(),
                                    it.errorBody().toString(),
                                    null
                                )
                            )
                        )
                    }
                }

            }
        }
    }
}