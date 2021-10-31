package com.auditionstreet.artist.model.response

import com.google.gson.annotations.SerializedName

data class MyProjectResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: ArrayList<Data>,
    @SerializedName("msg")
    val msg: String
) {
    data class Data(
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
        val updatedAt: String,
        @SerializedName("castingStatus")
        val castingStatus: String?,
        @SerializedName("projectId")
        val projectId: String?
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
}


