package com.payzone.binding.client

import android.os.Message
import com.google.gson.Gson
import com.payzone.transaction.client.ApiClient
import org.json.JSONObject
import kotlin.reflect.KClass

object TransactionServiceHelper {

    /**
     * Convert Type T(inline cast) to Json Object
     */
    inline fun <reified T : Any> convertToJson(value: T): JSONObject {
        return JSONObject(Gson().toJson(value))
    }

    /**
     * <reified T: Any> cast to any type
     * @param msg Message as received from Transaction Service
     * @param key Key of string response to be copied as json
     * @param typeClass class type for json to be serialized as
     *
     * Take String response, Convert to json
     * Convert Json response to Data class type and return
     *
     * @return Class<T: Any> will be returned same as type class specified in typeClass param
     */
    inline fun <reified T : Any> handleResponse(
        msg: Message, key: String, typeClass: KClass<T>
    ): T? {
        val response = ApiClient.decompressData(msg.data.getString(key))
        val initialisedResponse = Gson().fromJson(response, typeClass.java)
        println("$key = $response")
        return initialisedResponse
    }

}