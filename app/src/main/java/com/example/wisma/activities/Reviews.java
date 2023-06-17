package com.example.wisma.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wisma.R;
import com.example.wisma.adapter.ReviewAdapter;
import com.example.wisma.model.ModelReview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Reviews extends AppCompatActivity implements ReviewAdapter.OnItemClickListener {
    private static final int REQUEST_CODE_UPDATE_DATA = 1;
    private ImageButton btn_add, btn_back;
    private RecyclerView reviews;
    private ReviewAdapter reviewAdapter;
    private List<ModelReview> reviewList;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviews);

        reviews = findViewById(R.id.rv_reviews);
        btn_back = findViewById(R.id.btn_back);
        btn_add = findViewById(R.id.btn_add);

        mAuth = FirebaseAuth.getInstance();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (v.getId() == R.id.btn_back) {
//                    logOut();
//                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_add) {
                    Intent intent = new Intent(Reviews.this, AddReview.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        reviewAdapter.setOnItemClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        reviews.setLayoutManager(layoutManager);
        reviews.setAdapter(reviewAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("reviews").child(mAuth.getUid());

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelReview review = snapshot.getValue(ModelReview.class);
                    reviewList.add(review);
                }
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        databaseReference.addValueEventListener(valueEventListener);
    }

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadReviews();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("com.example.firebase.UPDATE_REVIEWS_LIST");
        registerReceiver(updateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }

    private void loadReviews() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reviewList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    ModelReview review = userSnapshot.getValue(ModelReview.class);
                    reviewList.add(review);
                }
                reviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Reviews.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_DATA && resultCode == RESULT_OK) {
            loadReviews();
        }
    }

    @Override
    public void onItemClick(ModelReview review) {
        Intent intent = new Intent(Reviews.this, UpdateReview.class);
        intent.putExtra("author", review.getAuthor());
        intent.putExtra("location", review.getLocation());
        intent.putExtra("review", review.getDescription());
        intent.putExtra("allreview", review);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_DATA);
    }

    public void logOut() {
        mAuth.signOut();
        Intent intent = new Intent(Reviews.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}