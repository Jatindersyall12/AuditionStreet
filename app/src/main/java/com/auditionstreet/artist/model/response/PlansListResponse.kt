package com.auditionstreet.artist.model.response
import com.google.gson.annotations.SerializedName


data class PlansListResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("msg")
    val msg: String
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("planPurchased")
        val planPurchased: String,
        @SerializedName("price")
        val price: String,
        @SerializedName("requests")
        val requests: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}