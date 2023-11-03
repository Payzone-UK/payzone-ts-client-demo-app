package com.payzone.binding.client

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.payzone.binding.client.response.AddCreditResponse
import com.payzone.binding.client.response.ReadKeyResponse
import com.payzone.transaction.client.ApiClient
import com.payzone.transaction.client.MessageConstants
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), ApiResponseListener {
    private var apiClient: ApiClient? = null
    private var responseHandler: ResponseHandler? = null
    private var replyMessenger: Messenger? = null
    private var buttonAddCredit: Button? = null
    private var keyImage: String? = null
    private var productId: Int? = null
    private var buttonReadKey: Button? = null
    private val mHandleMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isKeyInserted =
                intent.extras!!.getBoolean(MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED)
            sendKeyInserted(isKeyInserted)
        }
    }
    private val mHandleBoxStatusMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isBoxConnected =
                intent.extras!!.getBoolean(MessageConstants.RESP_TALEXUS_BOX_STATUS)
            isBoxConnected(isBoxConnected)
        }
    }

    private fun isBoxConnected(isBoxConnected: Boolean) {
//        if (isBoxConnected)
//            Toast.makeText(
//                getApplicationContext(),
//                "isBoxConnected :::::: " + isBoxConnected,
//                Toast.LENGTH_LONG
//            ).show();
        Log.d("isBoxConnected", "isBoxConnected: $isBoxConnected")
    }

    private fun sendKeyInserted(isKeyInserted: Boolean) {
//        if (isKeyInserted)
//            Toast.makeText(
//                getApplicationContext(),
//                "Key Inserted :::::: " + isKeyInserted,
//                Toast.LENGTH_LONG
//            ).show();
        Log.d("isKeyInserted", "isKeyInserted: $isKeyInserted")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mHandleMessageReceiver)
        unregisterReceiver(mHandleBoxStatusMessageReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        setContentView(R.layout.activity_main)
        buttonAddCredit = findViewById(R.id.buttonAddCredit)
        buttonReadKey = findViewById(R.id.buttonReadKey)
        registerReceiver(mHandleMessageReceiver, IntentFilter(MessageConstants.ACTION_KEY_INSERTED))
        registerReceiver(
            mHandleBoxStatusMessageReceiver,
            IntentFilter(MessageConstants.ACTION_TALEXUS_BOX_STATUS)
        )
        initService()
        buttonAddCredit?.setOnClickListener({
            try {
                addCredit()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        buttonReadKey?.setOnClickListener({
            try {
                readKey()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun initService() {
        responseHandler = ResponseHandler()
        replyMessenger = Messenger(responseHandler)
        responseHandler!!.setListener(this)
        apiClient = ApiClient(applicationContext, replyMessenger)
        apiClient!!.initService()
        Log.d("TAG", "initService: ")
    }

    @Throws(JSONException::class)
    fun registerDevice() {
        val obj = JSONObject()
        obj.put("barcode", "267693243349691")
        obj.put("deviceId", "1545D2053")
        obj.put("tId", "49691")
        val success = apiClient!!.registerDevice(obj)
        println("## Device Registration sent to service queue: $success")
    }

    @Throws(JSONException::class)
    fun startSession() {
        val obj = JSONObject()
        obj.put("pin", "2580")
        val success = apiClient!!.startSession(obj)
        println("## Start session sent to service queue: $success")
    }

    @Throws(JSONException::class)
    fun initTransaction() {
        val obj = JSONObject()
        obj.put("clientRef", "29fa53da-fea0-47b3-b7b0-ff564ed76324")
        obj.put("transactionSource", 0)
        obj.put("transactionGuid", "deb1f651-66ce-11eb-863b-a5942ff6aeb3")
        //        obj.put("productId", "3789");
        obj.put("transactionAmount", 1000)
        obj.put("barcode", "63385450042016567880")
        val success = apiClient!!.initTransaction(obj)
        println("## Transaction init sent to service queue: $success")
    }

    @Throws(JSONException::class)
    fun completeTransaction() {
        val obj = JSONObject()
        val extraJsonInfo = JSONObject()
        extraJsonInfo.put("basketId", "0423502170")
        extraJsonInfo.put("dateReceived", "2021-08-03")
        obj.put("extra_json_info", extraJsonInfo)
        obj.put("ticketNumber", "PP001002")
        obj.put("id", "deb1f651-66ce-11eb-863b-a5942ff6aeb3")
        obj.put("responseCode", "00")
        val success = apiClient!!.completeTransaction(obj)
        println("## Transaction complete sent to service queue: $success")
    }

    @Throws(JSONException::class)
    fun completeTransactionFailed() {
        val obj = JSONObject()
        obj.put("smartMeterErrorText", "Failed reason")
        obj.put("id", "deb1f651-66ce-11eb-863b-a5942ff6aeb3")
        obj.put("responseCode", "05")
        val success = apiClient!!.completeTransaction(obj)
        println("## Transaction complete failed sent to service queue: $success")
    }

    @Throws(JSONException::class)
    fun markTransactionSuccess() {
        val obj = JSONObject()
        obj.put("transactionGuid", "deb1f651-66ce-11eb-863b-a5942ff6aeb3")
        val success = apiClient!!.markTransactionSuccess(obj)
        println("## Mark transaction success sent to service queue: $success")
    }

    @Throws(JSONException::class)
    fun markTransactionFailed() {
        val obj = JSONObject()
        obj.put("transactionGuid", "deb1f651-66ce-11eb-863b-a5942ff6aeb3")
        val success = apiClient!!.markTransactionFailed(obj)
        println("## Mark transaction failed sent to service queue: $success")
    }

    @Throws(JSONException::class)
    fun markReceiptPrinted() {
        val obj = JSONObject()
        obj.put("transactionGuid", "deb1f651-66ce-11eb-863b-a5942ff6aeb3")
        val success = apiClient!!.markReceiptPrinted(obj)
        println("## Mark receipt printed sent to service queue: $success")
    }

    fun storeCashierId() {
        val success = apiClient!!.storeCashierId("101")
        println("## Store Cashier ID sent to service queue: $success")
    }

    val isTransactionReady: Unit
        get() {
            if (apiClient!!.mBound) {
                val success = apiClient!!.isTransactionReady
                println("## Is Transaction Ready sent to service queue: $success")
            }
        }
    val apiToken: Unit
        get() {
            val success = apiClient!!.getToken("35112029")
            println("## getApiToken: $success")
        }
    val isKeyInserted: Unit
        get() {
            if (apiClient!!.mBound) {
                val success = apiClient!!.isKeyInserted()
                println("## isKeyInserted: $success")
            }
        }
    val isBoxConnected: Unit
        get() {
            if (apiClient!!.mBound) {
                val success = apiClient!!.isBoxConnected()
                println("## isBoxConnected: $success")
            }
        }

    //
    @Throws(JSONException::class)
    fun readKey() {
        val success = apiClient!!.readKey()
        println("## readKey: $success")
    }

    override fun readKeyResponse(response: ReadKeyResponse?) {
        response?.success?.let {
            if (it) {
                productId = response.variants?.find { it.uiFlow == "talexus.addCredit" }?.id
                val balance =
                    response.variants?.find { it.uiFlow == "talexus.displayBalance" }?.balance
                keyImage = response.keyImage
                println("## productId: $productId")
                println("## keyImage: ${response.keyImage}")
                Toast.makeText(this, "Key read. Balance $balance", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun addCreditResponse(response: AddCreditResponse?) {
        response?.success?.let {
            if (it) {
                if (response.keyImage != null) keyImage = response.keyImage
            } else {
                Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    @Throws(JSONException::class)
    fun addCredit() {
        val obj = JSONObject()
        obj.put("amount", "300")
        obj.put("productId", productId)
        obj.put("keyImage", keyImage)
        val success = apiClient!!.addCredit(obj)
        println("## Talexus add credit: $success")
    }

    @Throws(JSONException::class)
    fun rti() {
        val obj = JSONObject()
        obj.put("rtiReference", "00757141")
        obj.put("productId", 11)
        obj.put("keyImage", keyImage)
        val success = apiClient!!.rti(obj)
        println("## Talexus RTI: $success")
    }

    @Throws(JSONException::class)
    fun reversal() {
        val obj = JSONObject()
        obj.put("productId", productId)
        obj.put("keyImage", keyImage)
        val success = apiClient!!.reversal(obj)
        println("## Talexus Reversal: $success")
    }

    @Throws(JSONException::class)
    fun securityKeys() {
        val obj = JSONObject()
        obj.put("serialNumber", "4gwlhfMKWzE=")
        val success = apiClient!!.securityKeys(obj)
        println("## securityKeys: $success")
    }

    override fun onStart() {
        super.onStart()
        try {

//          registerDevice();
//          startSession();

//          storeCashierId();
//          getApiToken();
//          securityKeys();
//            isTransactionReady
//            isKeyInserted
//            isBoxConnected

//          initTransaction();
//         completeTransaction();
//          completeTransactionFailed();
//          markTransactionSuccess();
//          markTransactionFailed();
//         markReceiptPrinted();
//            isKeyInserted();
//            readKey();
//            addCredit();
//            reversal();
//            rti();
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
    }

    inner class ResponseHandler : Handler() {
        var responseListener: ApiResponseListener? = null
        fun setListener(responseListener: ApiResponseListener?) {
            this.responseListener = responseListener
        }

        override fun handleMessage(msg: Message) {
            val response: String
            when (msg.what) {
                MessageConstants.MSG_REGISTER_DEVICE -> {
                    response =
                        ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_REGISTER_DEVICE))
                    println("## Register Device Response Binding = $response")
                }

                MessageConstants.MSG_INIT_TRANSACTION -> {
                    response =
                        ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_INIT_TRANSACTION))
                    println("## Transaction Initialised Response = $response")
                }

                MessageConstants.MSG_COMPLETE_TRANS -> try {
                    response =
                        ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_COMPLETE_TRANS))
                    println("## Complete Transaction Response = $response")
                    val obj = JSONObject(response)
                    println("## CUSTOMER RECEIPT LENGTH: " + obj.getString("customerReceipt").length)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                MessageConstants.MSG_MARK_TRANS_SUCCESS -> {
                    response =
                        ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_MARK_TRANS_SUCCESS))
                    println("## Marked Successful Response = $response")
                }

                MessageConstants.MSG_MARK_TRANS_FAILED -> {
                    response =
                        ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_MARK_TRANS_FAILED))
                    println("## Marked Failed Response = $response")
                }

                MessageConstants.MSG_MARK_RECEIPT_PRINTED -> {
                    response =
                        ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_MARK_RECEIPT_PRINTED))
                    println("## Marked Receipt Printed Response = $response")
                }

                MessageConstants.MSG_STORE_CID -> {
                    response =
                        ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_STORE_CID))
                    println("## Store Cashier ID Response = $response")
                }

                MessageConstants.MSG_IS_TRANSACTION_READY -> {
                    response =
                        ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_IS_TRANSACTION_READY))
                    println("## Is Transaction Ready Response = $response")
                }

                MessageConstants.MSG_TALEXUS_IS_KEY_INSERTED -> {
                    response =
                        ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED))
                    println("## Is Talexus key inserted Response = $response")
                }

                MessageConstants.MSG_TALEXUS_READ_KEY -> {
                    response =
                        ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_TALEXUS_READ_KEY))
                    println("## Talexus Read key Response = $response")
                    responseListener?.readKeyResponse(
                        TransactionServiceHelper.handleResponse(
                            msg = msg,
                            key = MessageConstants.RESP_TALEXUS_READ_KEY,
                            typeClass = ReadKeyResponse::class
                        )
                    )
                }

                MessageConstants.MSG_TALEXUS_ADD_CREDIT -> {
                    response =
                        ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_TALEXUS_ADD_CREDIT))
                    println("## Talexus Add credit Response = $response")
                    responseListener?.addCreditResponse(
                        TransactionServiceHelper.handleResponse(
                            msg = msg,
                            key = MessageConstants.RESP_TALEXUS_ADD_CREDIT,
                            typeClass = AddCreditResponse::class
                        )
                    )
                }

                MessageConstants.MSG_QUANTUM_SECURITY_KEYS -> {
                    response =
                        ApiClient.decompressData(msg.data.getString(MessageConstants.RESP_QUANTUM_SECURITY_KEYS))
                    println("## Q Response = $response")
                }

                else -> super.handleMessage(msg)
            }
        }
    }
}