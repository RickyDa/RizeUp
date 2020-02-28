package com.rizeup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.rizeup.Queue.QueueActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CAMERA_CODE = 100;
    private static final String IMAGE_DATA = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deleteUsers();
        registerUser();
        findViewById(R.id.btnGetInQ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, REQUEST_CAMERA_CODE);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA_CODE);

            }
        });


    }

    private void deleteUsers() {
        // TODO:
    }

    private void registerUser() {
        // TODO:
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA_CODE && resultCode == RESULT_OK) {
            Intent startQ = new Intent(getApplicationContext(), QueueActivity.class);
            Bitmap image = null;

            if (data != null)
                image = (Bitmap) Objects.requireNonNull(data.getExtras()).get(IMAGE_DATA);
            //TODO:
            //  -implement registerUsers() for testing
            //  -implement deleteUsers() for testing
            //  -FLOW (of this function)
            //      - after image capture upload the current user
            //        to firebase and add the user to the Q
            //      - when photo the is in uploading state show progress bar.
            //      - when upload is copleted start Queue.QueueActivity
            startQ.putExtra(IMAGE_DATA , image);
            startActivity(startQ);
        }


    }

}
