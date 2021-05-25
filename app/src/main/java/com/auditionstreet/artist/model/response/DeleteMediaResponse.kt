package com.auditionstreet.artist.model.response

import com.google.gson.annotations.SerializedName

data class DeleteMediaResponse(
    @SerializedName("code")
    val code: Int?,
    @SerializedName("data")
    val `data`: List<Any?>?,
    @SerializedName("msg")
    val msg: String?
)