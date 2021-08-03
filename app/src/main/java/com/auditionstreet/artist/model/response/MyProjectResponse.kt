package com.auditionstreet.artist.model.response

import com.google.gson.annotations.SerializedName

data class MyProjectResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val `data`: ArrayList<Data>,
    @SerializedName("msg")
    val msg: String?
) {
    data class Data(
        @SerializedName("age")
        val age: String?,
        @SerializedName("bodyType")
        val bodyType: String?,
        @SerializedName("castingId")
        val castingId: String?,
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("exp")
        val exp: String?,
        @SerializedName("fromDate")
        val fromDate: String?,
        @SerializedName("gender")
        val gender: String?,
        @SerializedName("heightFt")
        val heightFt: String?,
        @SerializedName("heightIn")
        val heightIn: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("lang")
        val lang: String?,
        @SerializedName("location")
        val location: String?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("toDate")
        val toDate: String?,
        @SerializedName("updated_at")
        val updatedAt: String?
    )
}