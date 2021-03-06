package com.silo.api

import com.auditionstreet.artist.api.ApiConstant.Companion.ACCEPT_REJECT_PROJECT
import com.auditionstreet.artist.api.ApiConstant.Companion.ADD_GROUP
import com.auditionstreet.artist.api.ApiConstant.Companion.ADD_PROJECT
import com.auditionstreet.artist.api.ApiConstant.Companion.GET_PROJECTS
import com.auditionstreet.artist.api.ApiConstant.Companion.LOGIN
import com.auditionstreet.artist.api.ApiConstant.Companion.REPORT_CASTING
import com.auditionstreet.artist.api.ApiConstant.Companion.SIGN_UP
import com.auditionstreet.artist.api.ApiConstant.Companion.UPLOAD_MEDIA
import com.auditionstreet.artist.model.response.*
import com.silo.model.request.*
import com.silo.model.response.LoginResponse
import com.silo.model.response.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.util.*


interface ApiService {

    @POST(LOGIN)
    suspend fun userLogin(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST(ADD_PROJECT)
    suspend fun addProject(@Body request: AddProjectRequest): Response<AddProjectResponse>

    @GET
    suspend fun getMyProjects(@Url url: String): Response<MyProjectResponse>

    @GET
    suspend fun getProfile(@Url url: String): Response<ProfileResponse>

    @GET
    suspend fun deleteMedia(@Url url: String): Response<DeleteMediaResponse>

    @Multipart
    @POST(UPLOAD_MEDIA)
    suspend fun uploadMedia(
        @PartMap requestProfileUpdate: HashMap<String, RequestBody>,
        @Part photo: List<MultipartBody.Part?>,
        @Part profileImageFile: MultipartBody.Part?,
        @Part introVideoUpload: MultipartBody.Part?
    ): Response<UploadMediaResponse>

    @GET
    suspend fun getAllAdmin(@Url url: String): Response<AllAdminResponse>


    @GET
    suspend fun getAllUser(@Url url: String): Response<AllUsersResponse>

    @GET
    suspend fun getMyProjectDetail(@Url url: String): Response<MyProjectDetailResponse>

    @POST(GET_PROJECTS)
    suspend fun getProjects(@Body projectRequest: ProjectRequest): Response<ProjectResponse>

    @POST(ADD_GROUP)
    suspend fun addGroup(@Body groupRequest: AddGroupRequest): Response<AddGroupResponse>

    @GET
    suspend fun getProjects(@Url url: String): Response<ProjectResponse>


    @Multipart
    @POST(SIGN_UP)
    suspend fun signUp(
        @PartMap params: HashMap<String, RequestBody>,
        @Part photo: MultipartBody.Part?
    ): Response<SignUpResponse>

    @POST(ACCEPT_REJECT_PROJECT)
    suspend fun acceptRejectProject(@Body acceptRejectProjectRequest: AcceptRejectProjectRequest): Response<AddGroupResponse>

    @GET
    suspend fun getHomeData(@Url url: String): Response<HomeApiResponse>

    @POST(REPORT_CASTING)
    suspend fun reportCasting(@Body reportCastingRequest: ReportCastingRequest): Response<AddGroupResponse>

    @GET
    suspend fun getOtherProfile(@Url url: String): Response<OtherProfileResponse>

    @GET
    suspend fun getAllProjects(@Url url: String): Response<MyProjectResponse>

    @GET
    suspend fun getLanguageBodyType(@Url url: String): Response<GetBodyTypeLanguageResponse>
}