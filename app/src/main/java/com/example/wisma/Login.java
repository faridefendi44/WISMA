package com.example.wisma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    EditText logusername, logpassword;
    MaterialButton loginbtn;
    TextView register;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mAuth = FirebaseAuth.getInstance();

        logusername = findViewById(R.id.logusername);
        logpassword = findViewById(R.id.logpassword);
        loginbtn = findViewById(R.id.loginbtn);
        progressBar = findViewById(R.id.progressBar);
        register = findViewById(R.id.signup);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(logusername.getText());
                password = String.valueOf(logpassword.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this, "Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }else{

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Authentication succes.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Login.this, Dashboard.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }
            }
        });


    }
}