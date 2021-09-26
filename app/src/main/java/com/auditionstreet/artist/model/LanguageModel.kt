package com.auditionstreet.artist.model

import com.google.gson.annotations.SerializedName

class LanguageModel {
    @SerializedName("isChecked")
    var isChecked: Boolean = false

    @SerializedName("language")
    var language: String = ""
}