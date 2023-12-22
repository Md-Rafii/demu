package com.rafii.send_sms;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView sms;

    public static String sendSMS="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sms=findViewById(R.id.sms);

        askPermissions();

        //askSMSPermission();
        initfirebaseToken();

    }





    public void initfirebaseToken(){

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        Log.d("firebaseToken",token);
                    }
                });


    }



    // Declare the launcher at the top of your Activity/Fragment:
    private static final int PERMISSION_REQUEST_CODE = 1;

    private void askPermissions() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissionArrays = new String[]{
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.POST_NOTIFICATIONS
            };

            List<String> permissionsToRequest = new ArrayList<>();

            for (String permission : permissionArrays) {
                if (ContextCompat.checkSelfPermission(this, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }

            if (permissionsToRequest.isEmpty()) {
                // All permissions are already granted
                // FCM SDK (and your app) can post notifications.
            } else {
                String rationaleMessage = "App ta sundor vabe chalanor jonno permission ta dorkar";

                // Check if you need to show rationale for any of the permissions
                for (String permission : permissionsToRequest) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        showRationaleDialog(rationaleMessage, permissionsToRequest);
                        return;
                    }
                }

                // Directly ask for the permissions
                ActivityCompat.requestPermissions(this,
                        permissionsToRequest.toArray(new String[0]),
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void showRationaleDialog(String message, final List<String> permissionsToRequest) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Permission Request")
                .setMessage(message)
                .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                permissionsToRequest.toArray(new String[0]),
                                PERMISSION_REQUEST_CODE);
                    }
                })
                .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle user's response accordingly
                    }
                })
                .create()
                .show();
    }

}