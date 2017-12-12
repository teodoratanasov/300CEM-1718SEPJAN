package com.example.admin.restaurantapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AccountForm extends AppCompatActivity {

    private ImageButton nImageSetup;
    private EditText nFieldName;
    private Button nSetupButton;

    private Uri nImageUri = null;

    private DatabaseReference nDatabaseusers;

    private FirebaseAuth nAuthentication;

    private StorageReference nStoreImage;

    private ProgressDialog nProgressDialog;

    private static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_form);

        /*
        connecting to the database
         */

        nAuthentication = FirebaseAuth.getInstance();
        /*
        storing the profile picture in the database
         */

        nStoreImage = FirebaseStorage.getInstance().getReference().child("Profile_images");
        /*
        storing the users in the database
         */

        nDatabaseusers = FirebaseDatabase.getInstance().getReference().child("Users");

        nProgressDialog = new ProgressDialog(this);

        nImageSetup = (ImageButton) findViewById(R.id.ImageButton);
        nFieldName = (EditText) findViewById(R.id.NameField);
        nSetupButton = (Button) findViewById(R.id.SubmitButton);

        nSetupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startSetupAccount();

            }
        });
        /*
        using the gallery request to open gallery to upload image
         */

        nImageSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);


            }
        });
    }

    private void startSetupAccount() {

        final String name = nFieldName.getText().toString().trim();

        final String user_id = nAuthentication.getCurrentUser().getUid();

        if (!TextUtils.isEmpty(name) && nImageUri != null) {

            nProgressDialog.setMessage("Finishing Setup ...");
            nProgressDialog.show();

            StorageReference filepath = nStoreImage.child(nImageUri.getLastPathSegment());

            filepath.putFile(nImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    nDatabaseusers.child(user_id).child("name").setValue(name);
                    nDatabaseusers.child(user_id).child("image").setValue(downloadUri);

                    nProgressDialog.dismiss();
                    /*
                    redirecting to post page
                     */

                    Intent mainIntent = new Intent(AccountForm.this, RestaurantApp.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            });
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        adding the crop image tool in order the user to crop his profile picture

         */


        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                nImageUri = result.getUri();

                nImageSetup.setImageURI(nImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
