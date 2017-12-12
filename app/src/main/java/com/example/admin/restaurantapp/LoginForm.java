package com.example.admin.restaurantapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginForm extends AppCompatActivity {

    /*
    setting some variables for the different functions
     */

    private EditText nloginEmailField;
    private EditText nloginPasswordField;
    private Button nloginButton;
    private Button nregisterButton;

    private FirebaseAuth nAuthentication;

    private ProgressDialog nProgress;

    private DatabaseReference nDatabaseUser;

    private SignInButton nGoogleButton;

    private static final int RC_SIGN_IN = 1;

    private static final String TAG = "LoginForm";

    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);

        nAuthentication = FirebaseAuth.getInstance();

        nProgress = new ProgressDialog(this);

        nGoogleButton = (SignInButton) findViewById(R.id.googleBtn);

        nDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users");
        nDatabaseUser.keepSynced(true);

        nloginEmailField = (EditText) findViewById(R.id.loginemailField);
        nloginPasswordField = (EditText) findViewById(R.id.loginpasswordField);
        nloginButton = (Button) findViewById(R.id.email);
        nregisterButton = (Button) findViewById(R.id.button3);

        nregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent registerIntent = new Intent(LoginForm.this, RegisterForm.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(registerIntent);
            }
        });

        nloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkLogin();

            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        nGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();

            }
        });


    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            nProgress.setMessage("Starting Sign in...");
            nProgress.show();

            if(result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {

                nProgress.dismiss();


                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount accct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + accct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(accct.getIdToken(), null);
        nAuthentication.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete"+ task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginForm.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        } else {

                            nProgress.dismiss();

                            checkUserExist();
                        }



                    }
                });
    }


    private void checkLogin() {

        String email = nloginEmailField.getText().toString().trim();
        String password = nloginPasswordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            nProgress.setMessage("Checking Login ...");
            nProgress.show();

            nAuthentication.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        nProgress.dismiss();

                        checkUserExist();

                    } else {

                        nProgress.dismiss();
                        Toast.makeText(LoginForm.this, "Error Login", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void checkUserExist() {

        final String user_id = nAuthentication.getCurrentUser().getUid();

        nDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(user_id)){

                    Intent mainIntent = new Intent(LoginForm.this, RestaurantApp.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                } else {

                    Intent setupIntent = new Intent(LoginForm.this, AccountForm.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(setupIntent);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
