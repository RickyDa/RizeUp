package com.rizeup.SignUp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rizeup.MainMenu.MainMenu;
import com.rizeup.R;
import com.rizeup.RiZeUpActivity;
import com.rizeup.models.RiZeUpUser;
import com.rizeup.utils.FileHandler;
import com.rizeup.utils.FirebaseReferences;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends RiZeUpActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private EditText email, fullName, password, rePassword;
    private CircleImageView profileImg;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        // Initialize Firebase instances
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_USERS);
        this.mStorage = FirebaseStorage.getInstance().getReference(FirebaseReferences.STORAGE_USER_IMAGE);

        this.email = findViewById(R.id.et_email);
        this.fullName = findViewById(R.id.et_full_name);
        this.password = findViewById(R.id.et_password);
        this.rePassword = findViewById(R.id.et_retype_password);
        this.profileImg = findViewById(R.id.profileImg);
        this.progressBar = findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.INVISIBLE);

        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: put VALIDATION on editText fields
                signUpUserAccount();
            }
        });
        findViewById(R.id.captureBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCameraPermission()) {
                    openCamera();
                }else {
                    requestPermission(CAMERA_CODE_REQUEST);
                }
            }
        });

        findViewById(R.id.uploadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkStoragePermission()) {
                    openFileChooser();
                }else{
                    requestPermission(STORAGE_CODE_REQUEST);
                }

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            assert data != null;
            if (requestCode == STORAGE_CODE_REQUEST && data.getData() != null) {
                this.imageUri = data.getData();
            }
            Glide.with(this).load(this.imageUri).into(this.profileImg);
        }
    }

    private void signUpUserAccount() {
        String pw = password.getText().toString();
        if (!(pw.equals(rePassword.getText().toString()))) {
            Toast.makeText(this, "Please retype password", Toast.LENGTH_SHORT).show();
            password.getText().clear();
            rePassword.getText().clear();
        }else if( pw.trim().isEmpty() || rePassword.getText().toString().trim().isEmpty() || fullName.getText().toString().trim().isEmpty() || email.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all the fields!", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    createRiZeUpUser();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Registration Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void createRiZeUpUser() {
        Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_LONG).show();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName.getText().toString()).setPhotoUri(imageUri).build();
        Objects.requireNonNull(mAuth.getCurrentUser()).updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                uploadUserImage();
            }
        });

    }

    private void uploadUserImage() {
        if (this.imageUri != null) {
            final StorageReference stoRef = mStorage.child(mAuth.getUid() + "." + FileHandler.getFileExtension(getContentResolver(), this.imageUri));
            stoRef.putFile(this.imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.INVISIBLE);
                            stoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    createDatabaseEntry(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Queueing Failed" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            if (progressBar.getVisibility() == View.INVISIBLE)
                                progressBar.setVisibility(View.VISIBLE);
                        }
                    });
        } else {
            createDatabaseEntry("");
        }
    }

    private void createDatabaseEntry(String downloadUrl) {
        RiZeUpUser newUser = new RiZeUpUser(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName(), downloadUrl, mAuth.getCurrentUser().getUid(),null);
        DatabaseReference child = mDatabase.child(mAuth.getCurrentUser().getUid());
        child.setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(getApplicationContext(), MainMenu.class));
                finishAffinity();
            }
        });
    }
}
