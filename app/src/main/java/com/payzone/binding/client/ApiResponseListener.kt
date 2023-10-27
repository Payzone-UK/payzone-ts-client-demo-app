package com.payzone.binding.client

import com.payzone.binding.client.response.AddCreditResponse
import com.payzone.binding.client.response.ReadKeyResponse

public interface ApiResponseListener {
    fun readKeyResponse(response: ReadKeyResponse?)
    fun addCreditResponse(response: AddCreditResponse?)
}