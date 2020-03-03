package com.rizeup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.rizeup.CreateQueue.CreateQueueActivity;
import com.rizeup.FindQueue.FindQueueActivity;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static final String USER_EXTRA = "user";
    private User theUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isStoragePermissionGranted();
        this.theUser = new User("Ricky","Rickyyy44@gmail.com","");

        findViewById(R.id.findBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findQueue = new Intent(getApplicationContext(), FindQueueActivity.class);
                startActivity(findQueue);
            }
        });

        findViewById(R.id.createBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createQueue = new Intent(getApplicationContext(), CreateQueueActivity.class);
                createQueue.putExtra(USER_EXTRA,theUser);
                startActivity(createQueue);
            }
        });
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission is granted");
                return true;
            } else {
                Log.d(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, 1);
                return false;
            }
        } else {
            Log.d(TAG, "Permission is granted");
            return true;
        }
    }

}
