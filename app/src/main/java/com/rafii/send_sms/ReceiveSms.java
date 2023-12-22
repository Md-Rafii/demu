package com.rafii.send_sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReceiveSms extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        Log.d("smsBroadcast", "onReceive called()");

        Toast.makeText(context,"recive :",Toast.LENGTH_LONG).show();

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String senderNum = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    long time = currentMessage.getTimestampMillis();
                    String smsTime = DateFormat.getDateTimeInstance().format(new Date(time));
                    Toast.makeText(context,message,Toast.LENGTH_LONG).show();



                    Log.e("smsBroadcast", "senderNum: " + senderNum);
                    Log.e("smsBroadcast", "message: " + message);
                    Log.e("smsBroadcast", "smsTime: " + smsTime);

                    //sending sms to your server
                    smsSendToServer(context, senderNum, message, smsTime);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,""+e,Toast.LENGTH_LONG).show();
        }
    }

    private void smsSendToServer(Context myContext, String senderNum, String message, String smsTime) {
        final String url = "https://mdraffi.000webhostapp.com/send_sms/update.php"; // Replace with your server URL

        RequestQueue MyRequestQueue = Volley.newRequestQueue(myContext);

        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("smsBroadcast", "Response: " + response);
                // Handle the server response as needed
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("smsBroadcast", "Error: " + error.getMessage());
                // Handle the error as needed
            }
        }) {

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<>();
                MyData.put("password", "01999842041");
                MyData.put("senderNum", senderNum);
                MyData.put("message", message);
                MyData.put("smsTime", smsTime);
                return MyData;
            }
        };

        MyRequestQueue.add(MyStringRequest);
    }
}
