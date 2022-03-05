package com.auditionstreet.artist.model.response
import com.google.gson.annotations.SerializedName


data class HomeApiResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("data")
    val `data`: Data
) {
    data class Data(
        @SerializedName("projectList")
        val projectList: List<Project>,
        @SerializedName("acceptList")
        val acceptList: List<Accept>,
        @SerializedName("pendingRequest")
        val pendingRequest: List<PendingRequest>,
        @SerializedName("isprofileupdated")
        val isprofileupdated: String
    ) {
        data class Project(
            @SerializedName("id")
            val id: Int,
            @SerializedName("castingId")
            val castingId: String,
            @SerializedName("title")
            val title: String,
            @SerializedName("description")
            val description: String,
            @SerializedName("gender")
            val gender: String,
            @SerializedName("age")
            val age: String,
            @SerializedName("heightFt")
            val heightFt: String,
            @SerializedName("heightIn")
            val heightIn: String,
            @SerializedName("bodyType")
            val bodyType: String,
            @SerializedName("exp")
            val exp: String,
            @SerializedName("lang")
            val lang: String,
            @SerializedName("fromDate")
            val fromDate: String,
            @SerializedName("toDate")
            val toDate: String,
            @SerializedName("location")
            val location: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("updated_at")
            val updatedAt: String
        )

        data class Accept(
            @SerializedName("title")
            val title: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("projectId")
            val projectId: String,
            @SerializedName("artistId")
            val artistId: String,
            @SerializedName("castingId")
            val castingId: String,
            @SerializedName("artistStatus")
            val artistStatus: String,
            @SerializedName("castingStatus")
            val castingStatus: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("artistName")
            val artistName: String,
            @SerializedName("heightFt")
            val heightFt: String,
            @SerializedName("heightIn")
            val heightIn: String,
            @SerializedName("age")
            val age: String,
            @SerializedName("video")
            val video: String,
            @SerializedName("projectAge")
            val projectAge: String,
            @SerializedName("projectheightFt")
            val projectheightFt: String,
            @SerializedName("projectheightIn")
            val projectheightIn: String,
            @SerializedName("projectgender")
            val projectgender: String,
            @SerializedName("castingEmail")
            val castingEmail: String
        )

        data class PendingRequest(
            @SerializedName("title")
            val title: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("projectId")
            val projectId: String,
            @SerializedName("artistId")
            val artistId: String,
            @SerializedName("castingId")
            val castingId: String,
            @SerializedName("artistStatus")
            val artistStatus: String,
            @SerializedName("castingStatus")
            val castingStatus: String,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("updated_at")
            val updatedAt: String,
            @SerializedName("artistName")
            val artistName: String,
            @SerializedName("heightFt")
            val heightFt: String,
            @SerializedName("heightIn")
            val heightIn: String,
            @SerializedName("age")
            val age: String,
            @SerializedName("video")
            val video: String,
            @SerializedName("projectAge")
            val projectAge: String,
            @SerializedName("projectheightFt")
            val projectheightFt: String,
            @SerializedName("projectheightIn")
            val projectheightIn: String,
            @SerializedName("projectgender")
            val projectgender: String
        )
    }
}