package com.payzone.binding.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.StrictMode;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import com.payzone.transaction.client.ApiClient;
import com.payzone.transaction.client.MessageConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.DataFormatException;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    ApiClient apiClient;
    ResponseHandler responseHandler;
    Messenger replyMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
    }

    private void initService() {
        responseHandler = new ResponseHandler();
        replyMessenger = new Messenger(responseHandler);
        apiClient = new ApiClient(getApplicationContext(), replyMessenger);
        apiClient.initService();
    }

    public void registerDevice() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("barcode", "267693243349691");
        obj.put("deviceId", "1545D2053");
        obj.put("tId", "49691");
        boolean success =  apiClient.registerDevice(obj);
        System.out.println("## Device Registration sent to service queue: "+success);
    }

    public void startSession() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("pin", "6632");
        boolean success =  apiClient.startSession(obj);
        System.out.println("## Start session sent to service queue: "+success);
    }

    public void initTransaction() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("clientRef", "29fa53da-fea0-47b3-b7b0-ff564ed76324");
//        obj.put("transactionSource", 0);
        obj.put("transactionGuid", "deb1f651-66ce-11eb-863b-a5942ff6aeb3");
//        obj.put("productId", "3789");
        obj.put("transactionAmount", 1000);
        obj.put("barcode", "63385450042016567880");
        boolean success =  apiClient.initTransaction(obj);
        System.out.println("## Transaction init sent to service queue: "+success);
    }

    public void completeTransaction() throws JSONException {
        JSONObject obj = new JSONObject();

        JSONObject extraJsonInfo = new JSONObject();
        extraJsonInfo.put("basketId", "0423502170");
        extraJsonInfo.put("dateReceived", "2021-08-03");
        obj.put("extra_json_info", extraJsonInfo);

        obj.put("ticketNumber", "PP001002");
        obj.put("id", "deb1f651-66ce-11eb-863b-a5942ff6aeb3");
        obj.put("responseCode", "00");

        boolean success =  apiClient.completeTransaction(obj);
        System.out.println("## Transaction complete sent to service queue: "+success);
    }

    public void completeTransactionFailed() throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("smartMeterErrorText", "Failed reason");
        obj.put("id", "deb1f651-66ce-11eb-863b-a5942ff6aeb3");
        obj.put("responseCode", "05");

        boolean success =  apiClient.completeTransaction(obj);
        System.out.println("## Transaction complete failed sent to service queue: "+success);
    }

    public void markTransactionSuccess() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("transactionGuid", "deb1f651-66ce-11eb-863b-a5942ff6aeb3");
        boolean success =  apiClient.markTransactionSuccess(obj);
        System.out.println("## Mark transaction success sent to service queue: "+success);
    }

    public void markTransactionFailed() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("transactionGuid", "deb1f651-66ce-11eb-863b-a5942ff6aeb3");
        boolean success =  apiClient.markTransactionFailed(obj);
        System.out.println("## Mark transaction failed sent to service queue: "+success);
    }

    public void markReceiptPrinted() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("transactionGuid", "deb1f651-66ce-11eb-863b-a5942ff6aeb3");
        boolean success =  apiClient.markReceiptPrinted(obj);
        System.out.println("## Mark receipt printed sent to service queue: "+success);
    }

    public void storeCashierId() {
        boolean success =  apiClient.storeCashierId("249");
        System.out.println("## Store Cashier ID sent to service queue: "+success);
    }

    public void getApiToken() {
        boolean success =  apiClient.getToken("4863025");
        System.out.println("## getApiToken: "+success);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initService();


        try {

//          registerDevice();
//          startSession();

//          storeCashierId();
//          getApiToken();

//          initTransaction();
 //         completeTransaction();
//          completeTransactionFailed();
//          markTransactionSuccess();
//          markTransactionFailed();
 //         markReceiptPrinted();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(apiClient.destroyService()) {
            System.out.println("## Disconnected from Payzone Transaction service...");
        }
    }

    public class ResponseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String response;
            switch (msg.what) {
                case MessageConstants.MSG_REGISTER_DEVICE:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_REGISTER_DEVICE));
                    System.out.println("## Register Device Response = "+response);
                    break;
                case MessageConstants.MSG_INIT_TRANSACTION:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_INIT_TRANSACTION));
                    System.out.println("## Transaction Initialised Response = "+response);
                    break;
                case MessageConstants.MSG_COMPLETE_TRANS:
                    try {
                        response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_COMPLETE_TRANS));
                        System.out.println("## Complete Transaction Response = " + response);

                        JSONObject obj = new JSONObject(response);
                        System.out.println("## CUSTOMER RECEIPT LENGTH: "+obj.getString("customerReceipt").length());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MessageConstants.MSG_MARK_TRANS_SUCCESS:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_MARK_TRANS_SUCCESS));
                    System.out.println("## Marked Successful Response = "+response);
                    break;
                case MessageConstants.MSG_MARK_TRANS_FAILED:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_MARK_TRANS_FAILED));
                    System.out.println("## Marked Failed Response = "+response);
                    break;
                case MessageConstants.MSG_MARK_RECEIPT_PRINTED:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_MARK_RECEIPT_PRINTED));
                    System.out.println("## Marked Receipt Printed Response = "+response);
                    break;
                case MessageConstants.MSG_STORE_CID:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_STORE_CID));
                    System.out.println("## Store Cashier ID Response = "+response);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
