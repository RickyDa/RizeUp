package com.rizeup.MainMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.rizeup.CreateQueue.CreateQueueActivity;
import com.rizeup.FindQueue.FindQueueActivity;
import com.rizeup.R;
import com.rizeup.utils.User;

public class MainMenu extends AppCompatActivity {
    private static final String TAG = "MainMenu.MainMenuActivity";
    public static final String USER_EXTRA = "user";
    private User theUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
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
}
