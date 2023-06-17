package com.example.wisma.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.wisma.R;
import com.example.wisma.model.ModelReview;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateReview extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private EditText updateReview, updateLocation;
    private TextView author;
    private ImageButton back, updateImage;
    private Button update, delete;
    private ModelReview review;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private static final  int Gallery_Code = 1;
    Uri imageUrl=null;
    String imageURL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_desc);

        author = findViewById(R.id.author);
        updateLocation = findViewById(R.id.location);
        updateReview = findViewById(R.id.review);
        updateImage = findViewById(R.id.previewImage);
        back = findViewById(R.id.btn_back);
        update = findViewById(R.id.btn_save);
        delete = findViewById(R.id.btn_del);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("reviews").child(mAuth.getUid());

        review = (ModelReview) getIntent().getSerializableExtra("allreview");
        if (review == null) {
            Toast.makeText(this, "Failed to get review from intent", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        author.setText(review.getAuthor());
        updateLocation.setText(review.getLocation());
        updateReview.setText(review.getDescription());
        Glide.with(this).load(review.getImgURL()).into(updateImage);

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });



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
        String updatedLocation = updateLocation.getText().toString().trim();
        String updatedDescription = updateReview.getText().toString().trim();

        if (updatedLocation.isEmpty()) {
            Toast.makeText(this, "Location cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (updatedDescription.isEmpty()) {
            Toast.makeText(this, "Review cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (review == null) {
            Toast.makeText(this, "Review is null, cannot update", Toast.LENGTH_SHORT).show();
            return;
        }

        review.setLocation(updatedLocation);
        review.setDescription(updatedDescription);

        // Jika terdapat gambar yang dipilih, unggah gambar ke Firebase Storage
        if (imageUrl != null) {
            uploadImage(review.getId());
        } else {
            // Jika tidak ada gambar yang dipilih, langsung perbarui data review di Firebase
            updateReviewData(review.getId());
        }
    }

    private void uploadImage(final String reviewId) {
        // Mendapatkan referensi storage di Firebase
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Membuat referensi file di Firebase Storage dengan nama unik berdasarkan timestamp
        StorageReference imageRef = storageRef.child("review_images/" + System.currentTimeMillis() + "." + getFileExtension(imageUrl));

        // Mengunggah file gambar ke Firebase Storage
        imageRef.putFile(imageUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Jika unggah gambar berhasil, mendapatkan URL gambar yang diunggah
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageURL = uri.toString();

                                // Set URL gambar pada objek review
                                review.setImgURL(imageURL);

                                // Perbarui data review di Firebase
                                updateReviewData(reviewId);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateReview.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateReviewData(String reviewId) {
        // Perbarui data review di Firebase Realtime Database
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
        String detailLocation = review.getLocation();
        String reviewDescription = review.getDescription();

        if (detailLocation.isEmpty()) {
            Toast.makeText(this, "Review description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

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
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Code && resultCode == RESULT_OK){
            imageUrl = data.getData();
            updateImage.setImageURI(imageUrl);
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}