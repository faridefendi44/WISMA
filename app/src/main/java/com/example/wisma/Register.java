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


public class Register extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    EditText regusername, name, regpassword;
    MaterialButton regbtn;
    TextView login;

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
        setContentView(R.layout.register);
        mAuth = FirebaseAuth.getInstance();

        regusername = findViewById(R.id.regusername);
//        name = findViewById(R.id.name);
        regpassword = findViewById(R.id.regpassword);
        regbtn = findViewById(R.id.signupbtn);
        progressBar = findViewById(R.id.progressBar);
        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, sname, password;
                email = String.valueOf(regusername.getText());
//                sname = String.valueOf(name.getText());
                password = String.valueOf(regpassword.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Register.this, "Enter Username", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if(TextUtils.isEmpty(sname)){
//                    Toast.makeText(Register.this, "Enter Name", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    Toast.makeText(Register.this, "Account created.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(Register.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });

    }
}