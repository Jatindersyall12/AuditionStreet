package com.silo.model.response
import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("msg")
    val msg: String?
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("deviceToken")
        val deviceToken: String?,
        @SerializedName("email")
        val email: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("image")
        val image: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("phoneNumber")
        val phoneNumber: String?,
        @SerializedName("socialId")
        val socialId: String?,
        @SerializedName("socialType")
        val socialType: String?,
        @SerializedName("updated_at")
        val updatedAt: String?
    )
}