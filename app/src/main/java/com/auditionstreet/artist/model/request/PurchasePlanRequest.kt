package com.silo.model.request
import com.google.gson.annotations.SerializedName


data class PurchasePlanRequest(
    var artistId: String = "",
    var planId: String = "",
    var paymentMode: String = "",
    var transactionId: String = ""
)