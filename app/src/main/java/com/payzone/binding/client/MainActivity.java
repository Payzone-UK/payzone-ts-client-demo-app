package com.payzone.binding.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

import org.json.JSONException;
import org.json.JSONObject;
import com.payzone.transaction.client.ApiClient;
import com.payzone.transaction.client.MessageConstants;

public class MainActivity extends AppCompatActivity {
    ApiClient apiClient;
    ResponseHandler responseHandler;
    Messenger replyMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        obj.put("barcode", "12349878388288288222219");
        obj.put("deviceId", "SN10289288299");
        obj.put("tId", "24728288");
        boolean success =  apiClient.registerDevice(obj);
        System.out.println("## Device Registration sent to service queue: "+success);
//        System.out.println("## From Server: "+messageResponseHandler.responseObject);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initService();
        try {
            registerDevice();
        } catch (JSONException e) {
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
        public JSONObject responseObject;

        @Override
        public void handleMessage(Message msg) {
            String response;
            switch (msg.what) {
                case MessageConstants.MSG_REGISTER_DEVICE:
                    response = msg.getData().getString(MessageConstants.RESP_REGISTER_DEVICE);
                    System.out.println("## Register Device Response = "+response);
                    break;
                case MessageConstants.MSG_INIT_TRANSACTION:
                    response = msg.getData().getString(MessageConstants.RESP_INIT_TRANSACTION);
                    System.out.println("## Transaction Initialised Response = "+response);
                    break;
                case MessageConstants.MSG_MARK_TRANS_SUCCESS:
                    response = msg.getData().getString(MessageConstants.RESP_MARK_TRANS_SUCCESS);
                    System.out.println("## Marked Successful Response = "+response);
                    break;
                case MessageConstants.MSG_MARK_TRANS_FAILED:
                    response = msg.getData().getString(MessageConstants.RESP_MARK_TRANS_FAILED);
                    System.out.println("## Marked Failed Response = "+response);
                    break;
                case MessageConstants.MSG_MARK_RECEIPT_PRINTED:
                    response = msg.getData().getString(MessageConstants.RESP_MARK_RECEIPT_PRINTED);
                    System.out.println("## Marked Receipt Printed Response = "+response);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
