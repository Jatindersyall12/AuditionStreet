package com.auditionstreet.artist.model.response

import com.google.gson.annotations.SerializedName

 data class MyProjectDetailResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("msg")
    val msg: String
) {
    data class Data(
        @SerializedName("admins")
        val admins: List<Admin>,
        @SerializedName("projectDetails")
        val projectDetails: ProjectDetails,
        @SerializedName("projectRequests")
        val projectRequests: List<ProjectRequest>
    ) {
        data class Admin(
            @SerializedName("bio")
            val bio: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("email")
            val email: String,
            @SerializedName("id")
            val id: String,
            @SerializedName("image")
            val image: String,
            @SerializedName("logo")
            val logo: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("password")
            val password: String,
            @SerializedName("pastWork")
            val pastWork: String,
            @SerializedName("socialId")
            val socialId: String,
            @SerializedName("socialType")
            val socialType: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("year")
            val year: String
        )

        data class ProjectDetails(
            @SerializedName("age")
            val age: String,
            @SerializedName("bodyType")
            val bodyType: List<BodyType>,
            @SerializedName("castingId")
            val castingId: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("deleteStatus")
            val deleteStatus: String,
            @SerializedName("description")
            val description: String,
            @SerializedName("exp")
            val exp: String,
            @SerializedName("fromDate")
            val fromDate: String,
            @SerializedName("gender")
            val gender: String,
            @SerializedName("heightFt")
            val heightFt: String,
            @SerializedName("heightIn")
            val heightIn: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("lang")
            val lang: List<Lang>,
            @SerializedName("location")
            val location: String,
            @SerializedName("skinTone")
            val skinTone: List<SkinTone>,
            @SerializedName("title")
            val title: String,
            @SerializedName("toDate")
            val toDate: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class BodyType(
                @SerializedName("id")
                val id: Int,
                @SerializedName("name")
                val name: String
            )

            data class Lang(
                @SerializedName("id")
                val id: Int,
                @SerializedName("name")
                val name: String
            )

            data class SkinTone(
                @SerializedName("id")
                val id: Int,
                @SerializedName("name")
                val name: String
            )
        }

        data class ProjectRequest(
            @SerializedName("age")
            val age: String,
            @SerializedName("artistId")
            val artistId: String,
            @SerializedName("artistImage")
            val artistImage: String,
            @SerializedName("artistName")
            val artistName: String,
            @SerializedName("artistStatus")
            val artistStatus: String,
            @SerializedName("castingId")
            val castingId: String,
            @SerializedName("castingStatus")
            val castingStatus: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("gender")
            val gender: String,
            @SerializedName("heightFt")
            val heightFt: String,
            @SerializedName("heightIn")
            val heightIn: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("projectId")
            val projectId: String,
            @SerializedName("title")
            val title: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("video")
            val video: String
        )
    }
}