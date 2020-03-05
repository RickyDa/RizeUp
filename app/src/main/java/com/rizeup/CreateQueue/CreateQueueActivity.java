package com.rizeup.CreateQueue;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rizeup.R;
import com.rizeup.utils.User;

import static com.rizeup.MainMenu.MainMenu.USER_EXTRA;


public class CreateQueueActivity extends AppCompatActivity {
    private static final String TAG = "CreateQueueActivity";
    private DatabaseReference databaseRef;
    private EditText mEditText;
    private Button mCreateQBtn;

    private User theUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_queue);
        this.databaseRef = FirebaseDatabase.getInstance().getReference("Queues");
        this.mCreateQBtn = findViewById(R.id.createQBtn);
        this.mEditText = findViewById(R.id.enter_q_name);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
// get data via the key
        this.theUser = (User) extras.get(USER_EXTRA);

        mCreateQBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createQueue();
                finish();
            }
        });
    }

    public void createQueue() {
        String qKey = this.databaseRef.push().getKey();
        RizeUpQueue q = new RizeUpQueue(mEditText.getText().toString(), theUser,qKey);
        DatabaseReference child = databaseRef.child(q.getName() + qKey);
        child.setValue(q);

    }
}
