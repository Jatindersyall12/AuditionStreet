package com.silo.model.request

import com.google.gson.annotations.SerializedName

class ReportCastingRequest {
    @SerializedName("message")
     var message: String = ""

    @SerializedName("artistId")
    var artistId: String = ""

    @SerializedName("castingId")
    var castingId: String = ""
}