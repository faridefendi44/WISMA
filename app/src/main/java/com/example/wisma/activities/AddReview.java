package com.example.wisma.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wisma.R;
import com.example.wisma.model.ModelReview;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddReview extends AppCompatActivity {
    private EditText addReview, addLocation;
    private ImageButton back, img;
    private Button save;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private static final  int Gallery_Code = 1;
    Uri imageUrl=null;
    String imageURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_desc);

        addLocation = findViewById(R.id.location);
        addReview = findViewById(R.id.review);
        back = findViewById(R.id.btn_back);
        save = findViewById(R.id.btn_save);
        img = findViewById(R.id.imgreview);
        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btn_back) {
                    Intent pindah = new Intent(AddReview.this, MainActivity.class);
                    startActivity(pindah);
                }
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Gallery_Code);
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

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("WISMA").child(imageUrl.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(AddReview.this);
        builder.setCancelable(false);
//        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(imageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                saveData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        String author = currentUser.getEmail();
//        String location = addLocation.getText().toString();
//        String review = addReview.getText().toString();
//        String reviewKey = databaseReference.child("reviews").child(mAuth.getUid()).push().getKey();
//        ModelReview baru = new ModelReview(reviewKey, author, location, review);
//        databaseReference.child("reviews").child(mAuth.getUid()).push().setValue(baru).addOnSuccessListener(this, new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                Toast.makeText(AddReview.this, "Add review", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(this, new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(AddReview.this, "Failed to add review", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void saveData(){

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String author = currentUser.getEmail();
        String location = addLocation.getText().toString();
        String review = addReview.getText().toString();

        String reviewKey = databaseReference.child("reviews").child(mAuth.getUid()).push().getKey();
        ModelReview baru = new ModelReview(reviewKey, author, location, review, imageURL);
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
        if (TextUtils.isEmpty(addLocation.getText().toString())) {
            addLocation.setError("Required");
            result = false;
        }
        if (TextUtils.isEmpty(addLocation.getText().toString())) {
            addLocation.setError("Required");
            result = false;
        }
        else {
            addReview.setError(null);
            addLocation.setError(null);
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Code && resultCode == RESULT_OK){
            imageUrl = data.getData();
            img.setImageURI(imageUrl);
        }
    }
}