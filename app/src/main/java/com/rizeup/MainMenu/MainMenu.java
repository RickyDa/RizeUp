package com.rizeup.MainMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rizeup.CreateQueue.CreateQueueActivity;
import com.rizeup.FindQueue.FindQueueActivity;
import com.rizeup.R;
import com.rizeup.utils.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainMenu extends AppCompatActivity {
    private static final String TAG = "MainMenu.MainMenuActivity";
    public static final String USER_EXTRA = "user";
    private User theUser;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private CircleImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        this.theUser = new User("Ricky","Rickyyy44@gmail.com","");

        this.mAuth = FirebaseAuth.getInstance();
        this.user = mAuth.getCurrentUser();
        this.profileImg = findViewById(R.id.userProfileImage);

        if(user.getPhotoUrl() != null) {
            Glide.with(this).load(user.getPhotoUrl()).into(this.profileImg);
        }
        else {
            Glide.with(this).load(R.drawable.defaultimage).into(this.profileImg);
            // TODO set listener to Profile pick for image capture
        }

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
}
