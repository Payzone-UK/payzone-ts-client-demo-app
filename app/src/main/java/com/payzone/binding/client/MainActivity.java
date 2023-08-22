package com.payzone.binding.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.StrictMode;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.payzone.transaction.client.ApiClient;
import com.payzone.transaction.client.MessageConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    ApiClient apiClient;
    ResponseHandler responseHandler;
    Messenger replyMessenger;
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean isKeyInserted = intent.getExtras().getBoolean(MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED);
            sendKeyInserted(isKeyInserted.toString());
        }
    };

    private void sendKeyInserted(String isKeyInserted) {
        Log.d("sendKeyInserted", "sendKeyInserted: "+ isKeyInserted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHandleMessageReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        registerReceiver(mHandleMessageReceiver, new IntentFilter(MessageConstants.ACTION_KEY_INSERTED));
    }

    private void initService() {
        responseHandler = new ResponseHandler();
        replyMessenger = new Messenger(responseHandler);
        apiClient = new ApiClient(getApplicationContext(), replyMessenger);
        apiClient.initService();
        Log.d("TAG", "initService: ");
    }

    public void registerDevice() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("barcode", "267693243349691");
        obj.put("deviceId", "1545D2053");
        obj.put("tId", "49691");
        boolean success = apiClient.registerDevice(obj);
        System.out.println("## Device Registration sent to service queue: " + success);
    }

    public void startSession() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("pin", "2580");
        boolean success = apiClient.startSession(obj);
        System.out.println("## Start session sent to service queue: " + success);
    }

    public void initTransaction() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("clientRef", "29fa53da-fea0-47b3-b7b0-ff564ed76324");
//        obj.put("transactionSource", 0);
        obj.put("transactionGuid", "deb1f651-66ce-11eb-863b-a5942ff6aeb3");
//        obj.put("productId", "3789");
        obj.put("transactionAmount", 1000);
        obj.put("barcode", "63385450042016567880");
        boolean success = apiClient.initTransaction(obj);
        System.out.println("## Transaction init sent to service queue: " + success);
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

        boolean success = apiClient.completeTransaction(obj);
        System.out.println("## Transaction complete sent to service queue: " + success);
    }

    public void completeTransactionFailed() throws JSONException {
        JSONObject obj = new JSONObject();

        obj.put("smartMeterErrorText", "Failed reason");
        obj.put("id", "deb1f651-66ce-11eb-863b-a5942ff6aeb3");
        obj.put("responseCode", "05");

        boolean success = apiClient.completeTransaction(obj);
        System.out.println("## Transaction complete failed sent to service queue: " + success);
    }

    public void markTransactionSuccess() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("transactionGuid", "deb1f651-66ce-11eb-863b-a5942ff6aeb3");
        boolean success = apiClient.markTransactionSuccess(obj);
        System.out.println("## Mark transaction success sent to service queue: " + success);
    }

    public void markTransactionFailed() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("transactionGuid", "deb1f651-66ce-11eb-863b-a5942ff6aeb3");
        boolean success = apiClient.markTransactionFailed(obj);
        System.out.println("## Mark transaction failed sent to service queue: " + success);
    }

    public void markReceiptPrinted() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("transactionGuid", "deb1f651-66ce-11eb-863b-a5942ff6aeb3");
        boolean success = apiClient.markReceiptPrinted(obj);
        System.out.println("## Mark receipt printed sent to service queue: " + success);
    }

    public void storeCashierId() {
        boolean success = apiClient.storeCashierId("101");
        System.out.println("## Store Cashier ID sent to service queue: " + success);
    }

    public void isTransactionReady() {
        boolean success = apiClient.isTransactionReady();
        System.out.println("## Is Transaction Ready sent to service queue: " + success);
    }

    public void getApiToken() {
        boolean success = apiClient.getToken("35112029");
        System.out.println("## getApiToken: " + success);
    }

    public void isKeyInserted() {
        boolean success = apiClient.isKeyInserted();
        System.out.println("## getApiToken: " + success);
    }

    public void readKey() throws JSONException {
        boolean success = apiClient.readKey();
        System.out.println("## readKey: " + success);
    }

    public void addCredit() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("amount", "300");
        obj.put("productId", "12");
        obj.put("keyImage", "333346574081DDBD0004B1B0130600B9000A77CB0000D3AC41E5FFAB4070C140990B440EC800840E040E040E040E040E040E040E040E440E040E440E040E040E22010100261E0D07000000000000395A000000000000000000000000800000180000000000005FE30000000000000000000000000000000000000000000056BC");
        boolean success = apiClient.addCredit(obj);
        System.out.println("## Talexus add credit: " + success);
    }

    public void rti() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("rtiReference", "00757141");
        obj.put("productId", "11");
        obj.put("keyImage", "33346574081DDBD0004926E120600AF000A47340000D3AC41E5FFAB4070C140990B440EC800840E040E04000180000000000005FE30000000000000000000000000000000000000000000056BC");
        boolean success = apiClient.rti(obj);
        System.out.println("## ReadKey 2: " + success);
    }

    public void reversal() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("productId", "11");
        obj.put("keyImage", "33346574081DDBD0004926E120600AF000A47340000D3AC41E5FFAB4070C140990B440EC800840E040E04000180000000000005FE30000000000000000000000000000000000000000000056BC");
        boolean success = apiClient.reversal(obj);
        System.out.println("## Reversal: " + success);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initService();


        try {

//            registerDevice();
//            startSession();
//
//            storeCashierId();
//            getApiToken();
//            isTransactionReady();
//
//            initTransaction();
//            completeTransaction();
//            completeTransactionFailed();
//            markTransactionSuccess();
//            markTransactionFailed();
//            markReceiptPrinted();
//            isKeyInserted();
//            readKey();
//            addCredit();
//            reversal();
//            rti();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public class ResponseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String response;
            switch (msg.what) {
                case MessageConstants.MSG_REGISTER_DEVICE:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_REGISTER_DEVICE));
                    System.out.println("## Register Device Response = " + response);
                    break;
                case MessageConstants.MSG_INIT_TRANSACTION:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_INIT_TRANSACTION));
                    System.out.println("## Transaction Initialised Response = " + response);
                    break;
                case MessageConstants.MSG_COMPLETE_TRANS:
                    try {
                        response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_COMPLETE_TRANS));
                        System.out.println("## Complete Transaction Response = " + response);

                        JSONObject obj = new JSONObject(response);
                        System.out.println("## CUSTOMER RECEIPT LENGTH: " + obj.getString("customerReceipt").length());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MessageConstants.MSG_MARK_TRANS_SUCCESS:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_MARK_TRANS_SUCCESS));
                    System.out.println("## Marked Successful Response = " + response);
                    break;
                case MessageConstants.MSG_MARK_TRANS_FAILED:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_MARK_TRANS_FAILED));
                    System.out.println("## Marked Failed Response = " + response);
                    break;
                case MessageConstants.MSG_MARK_RECEIPT_PRINTED:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_MARK_RECEIPT_PRINTED));
                    System.out.println("## Marked Receipt Printed Response = " + response);
                    break;
                case MessageConstants.MSG_STORE_CID:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_STORE_CID));
                    System.out.println("## Store Cashier ID Response = " + response);
                    break;
                case MessageConstants.MSG_IS_TRANSACTION_READY:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_IS_TRANSACTION_READY));
                    System.out.println("## Is Transaction Ready Response = " + response);
                    break;
                case MessageConstants.MSG_TALEXUS_IS_KEY_INSERTED:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_TALEXUS_IS_KEY_INSERTED));
                    System.out.println("## Is Talexus key inserted Response = " + response);
                    break;
                case MessageConstants.MSG_TALEXUS_READ_KEY:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_TALEXUS_READ_KEY));
                    System.out.println("## Talexus Read key Response = " + response);
                    break;
                case MessageConstants.MSG_TALEXUS_ADD_CREDIT:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_TALEXUS_ADD_CREDIT));
                    System.out.println("## Talexus Add credit Response = " + response);
                    break;
                case MessageConstants.MSG_TALEXUS_RTI:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_TALEXUS_RTI));
                    System.out.println("## Talexus RTI Response = " + response);
                    break;
                case MessageConstants.MSG_TALEXUS_REVERSE_CREDIT:
                    response = ApiClient.decompressData(msg.getData().getString(MessageConstants.RESP_TALEXUS_REVERSE_CREDIT));
                    System.out.println("## Talexus Reverse Response = " + response);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
