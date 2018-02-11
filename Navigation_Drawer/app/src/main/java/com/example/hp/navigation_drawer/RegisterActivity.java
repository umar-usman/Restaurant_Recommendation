package com.example.hp.navigation_drawer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText Name_field;
    private EditText Username_field;
    private EditText email_field;
    private EditText pwd_field;
    private EditText repwd_field;
    private EditText contactinfo;
    private RadioGroup rg;
    private RadioButton gender_btn;
    private EditText gender_info;
    private Button signup_btn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDB;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance().getReference().child("Users");

        mProgress = new ProgressDialog(this);

        Name_field = findViewById(R.id.Name_field);
        Username_field = findViewById(R.id.Username_field);
        email_field = findViewById(R.id.logInEmail_field);
        pwd_field = findViewById(R.id.pwd_field);
        repwd_field = findViewById(R.id.pwd_field2);
        contactinfo = findViewById(R.id.Contact_info);
        gender_info = findViewById(R.id.Gender_info);
//        rg = (RadioGroup)findViewById(R.id.r_Group);


//        gender_btn=(RadioButton)findViewById(rg.getCheckedRadioButtonId());


        signup_btn = findViewById(R.id.register_btn);

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startRegister();
            }
        });

    }

    private void startRegister(){
        final String name= Name_field.getText().toString().trim();
        final String username= Username_field.getText().toString().trim();
        final String email= email_field.getText().toString().trim();
        final String pwd= pwd_field.getText().toString().trim();
        final String repwd= repwd_field.getText().toString().trim();
        final String gender =  gender_info.getText().toString().trim();
        final String contact = contactinfo.getText().toString().trim();



        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(repwd))
        {
            if(pwd.equals(repwd))
            {
                mProgress.setMessage("Signing Up...");
                mProgress.show();
                mAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            String user_id = mAuth.getCurrentUser().getUid();

                            DatabaseReference current_User = mDB.child(user_id);

                            current_User.child("Name").setValue(name);
                            current_User.child("User Name").setValue(username);
                            current_User.child("Email").setValue(email);
                            current_User.child("Gender").setValue(gender);
                            current_User.child("Contact Number").setValue(contact);

                            //current_User.child("image").setValue("default");

                            mProgress.dismiss();

                            Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);


                        }

                    }
                });

            }
            else
            {
                Toast.makeText(getApplicationContext(), "password don't match!", Toast.LENGTH_SHORT).show();
                return;
                //show error pwd not matched
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
            //Show error fill all fields
        }


    }
//    public void rbclick (View v)
//    {
//        gender_btn=(RadioButton)findViewById(rg.getCheckedRadioButtonId());
//    }
}
