package com.rizeup.MainMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
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
import com.rizeup.Queue.QueueActivity;
import com.rizeup.R;
import com.rizeup.models.RiZeUpQueue;
import com.rizeup.models.RiZeUpUser;
import com.rizeup.utils.FirebaseReferences;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainMenu extends AppCompatActivity {
    private static final String TAG = "MainMenu.MainMenuActivity";

    private FirebaseUser theUser;
    private DatabaseReference userDatabaseRef;
    private DatabaseReference queue;
    private CircleImageView profileImg, queueImage;
    private TextView queueName, queueOwner, userPlace;
    private String queueOwnerUid;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        this.theUser = FirebaseAuth.getInstance().getCurrentUser();
        this.queue = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES);
        this.userDatabaseRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_USERS + "/" + theUser.getUid());

        this.profileImg = findViewById(R.id.userProfileImage);
        this.queueImage = findViewById(R.id.mainMenu_queueImage);
        this.queueName = findViewById(R.id.mainMenu_queueName);
        this.queueOwner = findViewById(R.id.mainMenu_queueOwner);
        this.userPlace = findViewById(R.id.mainMenu_queuePlace);


        loadImage();
        loadRegisteredQueue();
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
        findViewById(R.id.mainMenu_registeredQueue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QueueActivity.class);
                intent.putExtra(FindQueueActivity.QID_EXTRA, queueOwnerUid);
                startActivity(intent);
            }
        });


    }

    private void loadRegisteredQueue() {
        this.userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RiZeUpUser user = dataSnapshot.getValue(RiZeUpUser.class);
                if (user.getRegisteredQ() != null) {

                    queue.child(user.getRegisteredQ()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            RiZeUpQueue q = dataSnapshot.getValue(RiZeUpQueue.class);
                            queueOwnerUid = q.getOwnerUid();
                            queueName.setText(q.getName());
                            queueOwner.setText(q.getOwnerName());
                            Glide.with(getApplicationContext()).load(q.getImageUrl()).into(queueImage);
                            queue.child(queueOwnerUid + "/" + FirebaseReferences.REAL_TIME_DATABASE_PARTICIPANTS).orderByChild("timeStamp").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int count = 1;
                                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                                        RiZeUpUser u = s.getValue(RiZeUpUser.class);
                                        if (u.getUid().equals(theUser.getUid())) {
                                            userPlace.setText(String.valueOf(count));
                                            break;
                                        }
                                        count++;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            queueImage.setVisibility(View.VISIBLE);
                            userPlace.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    showDeafaultWindow();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showDeafaultWindow() {
        this.queueImage.setVisibility(View.INVISIBLE);
        this.queueName.setText("");
        this.queueOwner.setText("");
        this.userPlace.setVisibility(View.INVISIBLE);
    }

    private void loadImage() {
        DatabaseReference users = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_USERS).child(theUser.getUid());
        if (theUser.getPhotoUrl() != null) {
            users.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String uri = dataSnapshot.getValue(RiZeUpUser.class).getImageUri();
                    if (uri != null)
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

    @Override
    protected void onResume() {
        super.onResume();
        loadRegisteredQueue();
    }
}
