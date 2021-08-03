package com.silo.model.request
import com.google.gson.annotations.SerializedName

class AcceptRejectProjectRequest {

    @SerializedName("userStatus")
    var userStatus: String=""

    @SerializedName("status")
    var status: String=""

    @SerializedName("projectId")
    var id: String=""

    @SerializedName("artistId")
    var artistId: String=""
}