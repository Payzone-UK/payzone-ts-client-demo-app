package com.payzone.binding.client.response

import com.google.gson.annotations.SerializedName

data class ReadKeyResponse(

    @SerializedName("productId") var productId: Int? = null,
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("keyImage") var keyImage: String? = null,
    @SerializedName("variants") var variants: ArrayList<Variants> = arrayListOf()

)

data class AddCreditResponse(
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("keyImage") var keyImage: String? = null
)

data class RTIResponse(
    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("keyImage") var keyImage: String? = null
)

data class Variants(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("iin") var iin: String? = null,
    @SerializedName("balance") var balance: Int? = null,
    @SerializedName("maxAmount") var maxAmount: Int? = null,
    @SerializedName("multipleOf") var multipleOf: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("uiFlow") var uiFlow: String? = null

)