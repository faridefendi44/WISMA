package com.example.wisma.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wisma.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddReview extends AppCompatActivity {
    private EditText addReview;
    private ImageButton back;
    private Button save;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_desc);

        addReview = findViewById(R.id.review);
        back = findViewById(R.id.btn_back);
        save = findViewById(R.id.btn_save);

        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_back) {
                    Intent pindah = new Intent(AddReview.this, Reviews.class);
                    startActivity(pindah);
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_save) {
                    submitData();
                    Intent pindah = new Intent(AddReview.this, Reviews.class);
                    startActivity(pindah);
                }
            }
        });
    }

    public void submitData() {
        if (!validateForm()) {
            return;
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String author = currentUser.getEmail();
        String review = addReview.getText().toString();
        String reviewKey = databaseReference.child("reviews").child(mAuth.getUid()).push().getKey();
        Review baru = new Review(reviewKey, author, review);
        databaseReference.child("reviews").child(mAuth.getUid()).push().setValue(baru).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(AddReview.this, "Add review", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddReview.this, "Failed to add review", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(addReview.getText().toString())) {
            addReview.setError("Required");
            result = false;
        }
        else {
            addReview.setError(null);
        }
        return result;
    }
}