package com.rizeup.Queue;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rizeup.R;
import com.rizeup.utils.FirebaseReferences;

import java.util.ArrayList;
import java.util.Objects;

public class ManageActivity extends RiZeUpQueueActivity {

    private DatabaseReference usersRef;
    private LinearLayout delLayout, renameLayout;
    private FloatingActionButton fabDelete, fabRename, fabSettings;
    private boolean isOpen = false;
    private Animation openAnim, closeAnim, rotateForward, rotateBackward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        this.queueNameTextView = findViewById(R.id.manage_queueName);
        this.queueImageView = findViewById(R.id.manage_QueueImage);
        this.participants = new ArrayList<>();
        this.queueRecyclerView = findViewById(R.id.manage_queueRecyclerView);
        this.loaded = false;
        this.queueRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_QUEUES).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        this.usersRef = FirebaseDatabase.getInstance().getReference(FirebaseReferences.REAL_TIME_DATABASE_USERS);
        this.participantsRef = queueRef.child(FirebaseReferences.REAL_TIME_DATABASE_PARTICIPANTS);

        this.delLayout = findViewById(R.id.manage_fabLayout_delete);
        this.renameLayout = findViewById(R.id.manage_fabLayout_rename);

        this.fabDelete = findViewById(R.id.manage_fab_delete);
        this.fabRename = findViewById(R.id.manage_fab_rename);
        this.fabSettings = findViewById(R.id.manage_fab_settings);

        this.openAnim = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        this.closeAnim = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        this.rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        this.rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);

        initQueue();
        initItemTouchHelper();

        this.fabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFabs();
            }
        });

        this.fabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteQueue();
            }
        });

        this.fabRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renameQueue();
                animateFabs();
            }
        });

    }

    private void renameQueue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("enter an new name");
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                queueName = input.getText().toString();
                if (queueName.trim().equals(""))
                    Toast.makeText(ManageActivity.this, "Must give a name", Toast.LENGTH_SHORT).show();
                else {
                    queueRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().child("name").setValue(queueName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    queueNameTextView.setText(queueName);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void deleteQueue() {
        queueRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void animateFabs() {
        if (this.isOpen) {
            this.fabSettings.startAnimation(rotateForward);
            this.delLayout.startAnimation(closeAnim);
            this.renameLayout.startAnimation(closeAnim);
            this.fabDelete.setClickable(false);
            this.fabRename.setClickable(false);
            isOpen = false;
        } else {
            this.fabSettings.startAnimation(rotateBackward);
            this.delLayout.startAnimation(openAnim);
            this.renameLayout.startAnimation(openAnim);
            this.fabDelete.setClickable(true);
            this.fabRename.setClickable(true);
            isOpen = true;
        }
    }

    private void initItemTouchHelper() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                final int i = viewHolder.getAdapterPosition();
                participantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (snapshot.getKey().equals(participants.get(i).getUid())) {
                                snapshot.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        usersRef.child(snapshot.getKey() + "/" + FirebaseReferences.REAL_TIME_RIZE_UP_USER_REG).removeValue();
                                    }
                                });
                                participants.remove(i);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this.queueRecyclerView);
    }
}
