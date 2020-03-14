package com.rizeup.MainMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.CreateQueue.CreateQueueActivity;
import com.rizeup.FindQueue.FindQueueActivity;
import com.rizeup.Queue.ManageActivity;
import com.rizeup.R;
import com.rizeup.models.RiZeUpUser;
import com.rizeup.utils.FirebaseReferences;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainMenu extends AppCompatActivity {
    private static final String TAG = "MainMenu.MainMenuActivity";

    private FirebaseUser theUser;
    private DatabaseReference queue;
    private CircleImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        this.theUser = FirebaseAuth.getInstance().getCurrentUser();
        this.queue = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES);
        this.profileImg = findViewById(R.id.userProfileImage);

        loadImage();

        findViewById(R.id.findBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findQueue = new Intent(getApplicationContext(), FindQueueActivity.class);
                startActivity(findQueue);
            }
        });

        findViewById(R.id.manageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queue.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(theUser.getUid())) {
                            startActivity(new Intent(getApplicationContext(), ManageActivity.class));
                        } else {
                            startActivity(new Intent(getApplicationContext(), CreateQueueActivity.class));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

            }
        });
    }

    private void loadImage() {
        DatabaseReference users = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_USERS).child(theUser.getUid());
        if (theUser.getPhotoUrl() != null) {
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String uri = dataSnapshot.getValue(RiZeUpUser.class).getImageUri();
                    if(uri != null)
                        Glide.with(getApplicationContext()).load(uri).into(profileImg);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(), TAG + "Failed load Image", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Glide.with(this).load(R.drawable.defaultimage).into(this.profileImg);
        }
    }
}
