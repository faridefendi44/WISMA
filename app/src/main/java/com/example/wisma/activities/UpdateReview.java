package com.example.wisma.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wisma.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UpdateReview extends AppCompatActivity {
    private EditText updateReview;
    private TextView author;
    private ImageButton back;
    private Button update, delete;
    private Review review;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_desc);

        author = findViewById(R.id.author);
        updateReview = findViewById(R.id.review);
        back = findViewById(R.id.btn_back);
        update = findViewById(R.id.btn_save);
        delete = findViewById(R.id.btn_del);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("reviews").child(mAuth.getUid());

        review = (Review) getIntent().getSerializableExtra("allreview");
        if (review == null) {
            Toast.makeText(this, "Failed to get review from intent", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        author.setText(review.getAuthor());
        updateReview.setText(review.getDescription());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pindah = new Intent(UpdateReview.this, Reviews.class);
                startActivity(pindah);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReview();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReview();
            }
        });
    }

    private void updateReview() {
        String updatedDescription = updateReview.getText().toString().trim();

        if (updatedDescription.isEmpty()) {
            Toast.makeText(this, "Review cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (review == null) {
            Toast.makeText(this, "Review is null, cannot update", Toast.LENGTH_SHORT).show();
            return;
        }

        review.setDescription(updatedDescription);

        databaseReference.child(review.getId())
                .setValue(review)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateReview.this, "Review updated", Toast.LENGTH_SHORT).show();
                        sendUpdateSignal();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateReview.this, "Failed to update review", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteReview() {
        String reviewDescription = review.getDescription();

        if (reviewDescription.isEmpty()) {
            Toast.makeText(this, "Review description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Query query = databaseReference.orderByChild("description").equalTo(reviewDescription);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(UpdateReview.this, "Review deleted", Toast.LENGTH_SHORT).show();
                                    sendUpdateSignal();
                                    Intent intent = new Intent(UpdateReview.this, Reviews.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UpdateReview.this, "Failed to delete review", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateReview.this, "Failed to delete review", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendUpdateSignal() {
        Intent intent = new Intent("com.example.firebase.UPDATE_REVIEWS_LIST");
        sendBroadcast(intent);
    }
}