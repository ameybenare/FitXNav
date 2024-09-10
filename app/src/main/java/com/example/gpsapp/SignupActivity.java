package com.example.gpsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class SignupActivity extends AppCompatActivity {
Button register;
TextView login;
EditText email,password,repassword,username;
FirebaseAuth auth;

FirebaseDatabase database;
String emailPattern = "[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login2);
        email = findViewById(R.id.emailid);
        password = findViewById(R.id.password);
        repassword = findViewById(R.id.repassword);
        username = findViewById(R.id.username);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = username.getText().toString();
                String Email = email.getText().toString();
                String Pass = password.getText().toString();
                String repass = repassword.getText().toString();
                String status = "added";
                if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(Pass) || TextUtils.isEmpty(repass) || TextUtils.isEmpty(uname)){
                    Toast.makeText(SignupActivity.this, "Please enter valid information",Toast.LENGTH_SHORT).show();
                }else if(!Email.matches(emailPattern)){
                    email.setError("Enter a valid email");
                }else if(Pass.length() < 8 || repass.length() < 8){
                    password.setError("password should be more than 8 characters");
                }else if(!Pass.equals(repass)){
                    repassword.setError("Password doesn't match");
                }else
                {
                    auth.createUserWithEmailAndPassword(Email,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("user").child("id");
                                Users user = new Users(uname,Email,Pass,id,status);
                                reference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                            intent.putExtra("email",Email);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            Toast.makeText(SignupActivity.this, "Error in creating the user",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(SignupActivity.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

}