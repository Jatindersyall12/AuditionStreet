package com.auditionstreet.artist.model

import com.google.gson.annotations.SerializedName

class BodyTypeModel {
    @SerializedName("isChecked")
    var isChecked: Boolean = false

    @SerializedName("bodyType")
    var bodyType: String = ""
}