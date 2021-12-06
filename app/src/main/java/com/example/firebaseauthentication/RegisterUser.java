package com.example.firebaseauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;

    //View elements
    private TextView bannerView, loginView;
    private EditText nameView, ageView, emailView, passwordView;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        bannerView = (TextView) findViewById(R.id.banner);
        bannerView.setOnClickListener(this);

        loginView = (Button) findViewById(R.id.loginButton);
        loginView.setOnClickListener(this);

        nameView = (EditText) findViewById(R.id.name);
        ageView = (EditText) findViewById(R.id.age);
        emailView = (EditText) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.banner:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.loginButton:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = emailView.getText().toString().trim();
        String password = passwordView.getText().toString().trim();
        String name = nameView.getText().toString().trim();
        String age = ageView.getText().toString().trim();

        if(name.isEmpty()){
            nameView.setError("Full name is required");
            nameView.requestFocus();
        }

        if(age.isEmpty()){
            ageView.setError("Full name is required");
            ageView.requestFocus();
        }

        if(email.isEmpty()){
            emailView.setError("Full name is required");
            emailView.requestFocus();
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailView.setError("Please, introduce a valid email");
            emailView.requestFocus();
        }

        if(password.isEmpty()){
            passwordView.setError("Introduce a valid password");
            passwordView.requestFocus();
        }

        if(password.length() < 6){
            passwordView.setError("Password is required");
            passwordView.requestFocus();
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name,age,email);

                            FirebaseDatabase.getInstance().getReference("Users").
                                    child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this,"User has been registered succesfully!",Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.VISIBLE);


                                    }else{
                                        Toast.makeText(RegisterUser.this,"Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });

                        }else{
                            Toast.makeText(RegisterUser.this,"Failed to register! Try again!", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}