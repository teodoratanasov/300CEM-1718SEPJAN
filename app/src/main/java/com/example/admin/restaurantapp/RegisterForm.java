package com.example.admin.restaurantapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterForm extends AppCompatActivity {
    /*
    setting the different text fields and buttons
     */

    private EditText nNameField;
    private EditText nEmailField;
    private EditText nPasswordField;

    private Button nRegisterButton;
    private DatabaseReference nDatabasereference;

    private FirebaseAuth nAuthentication;
    private ProgressDialog nProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_form);
        /*
        fetching information from the database
         */

        nAuthentication = FirebaseAuth.getInstance();

        nDatabasereference = FirebaseDatabase.getInstance().getReference().child("Users");

        nProgressDialog = new ProgressDialog(this);


        nNameField = (EditText) findViewById(R.id.name);
        nEmailField = (EditText) findViewById(R.id.email);
        nPasswordField = (EditText) findViewById(R.id.password);

        nRegisterButton = (Button) findViewById(R.id.registerButton);

        nRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startRegister();

            }
        });
    }

    private void startRegister() {

        /*
        get the text from the text boxes
         */

        final String name = nNameField.getText().toString().trim();
        String email = nEmailField.getText().toString().trim();
        String password = nPasswordField.getText().toString().trim();
        /*
        check if fields are empty
         */

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            /*
            show a process dialog when registering
             */

            nProgressDialog.setMessage("Registering...");
            nProgressDialog.show();
            /*

             */

            nAuthentication.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        /*
                        if task is successfull add data to the database
                         */

                        String user_id = nAuthentication.getCurrentUser().getUid();

                        DatabaseReference current_user_db = nDatabasereference.child(user_id);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("image").setValue("default");

                        nProgressDialog.dismiss();
                        /*
                        redirect to the login form
                         */

                        Intent loginIntent = new Intent(RegisterForm.this, LoginForm.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);

                    }
                }
            });
        }
    }
}
