package com.example.hp.navigation_drawer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email_field;
    private EditText pwd_field;
    private Button logIn_btn;
    private Button Register_btn;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        email_field =(EditText) findViewById(R.id.logInEmail_field);
        pwd_field =(EditText) findViewById(R.id.logInpwd_field);

        logIn_btn =(Button) findViewById(R.id.signIn_btn);
        Register_btn =(Button) findViewById(R.id.signUp_btn);
        logIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkLogIn();
            }
        });
        Register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSignUp();
            }
        });
    }

    private void checkLogIn(){

        String email= email_field.getText().toString().trim();
        String pwd= pwd_field.getText().toString().trim();
        if(!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(email)){
            mAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        Intent SignInIntent =new Intent(LoginActivity.this,MainActivity.class);
                        SignInIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(SignInIntent);
                    }
                    else{

                        Toast.makeText(getApplicationContext(), "Error LogIn!", Toast.LENGTH_LONG).show();

                    }
                }
            });


        }


    }
    private void startSignUp(){

        Intent SignUpIntent =new Intent(LoginActivity.this,RegisterActivity.class);
        SignUpIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(SignUpIntent);

    }
}
