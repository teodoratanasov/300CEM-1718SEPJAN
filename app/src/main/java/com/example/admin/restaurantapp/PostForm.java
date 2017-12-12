package com.example.admin.restaurantapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostForm extends AppCompatActivity {

    private ImageButton nSelectedImage;
    private EditText nPostingTitle;
    private EditText nPostingDesc;

    private Button mSubmitBtn;

    private Uri mImageUri = null;
    /**
     * Making a gallery request to fetch images
     */
    private static final int GALLERY_REQUEST = 1;


    private StorageReference nStorageindatabase;
    private DatabaseReference nDatabase;

    private ProgressDialog nProgressDialog;

    private FirebaseAuth nAuthentication;

    private FirebaseUser nUser;

    private DatabaseReference nDatabaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_form);
        /*
        code using the camera by clicking the Take a Photo button
         */

        Button pictureBtn = (Button)findViewById(R.id.pictureBtn);

        pictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        nAuthentication = FirebaseAuth.getInstance();


        nUser = nAuthentication.getCurrentUser();
        /*
        connecting to the database and storing the information of the posts and the users
         */

        nStorageindatabase = FirebaseStorage.getInstance().getReference();
        nDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");

        nDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(nUser.getUid());

        nSelectedImage = (ImageButton) findViewById(R.id.imagePick);

        nPostingTitle = (EditText) findViewById(R.id.titleField);
        nPostingDesc = (EditText) findViewById(R.id.descField);

        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        /**
         * request images from gallery code
         */
        nProgressDialog = new ProgressDialog(this);

        nSelectedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }


        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPosting();

            }
        });
    }
    /*
    Posting the post to the app
     */

    private void startPosting() {
        /*
        adding a display dialog to show the progress of uploading the post to the app
         */

        nProgressDialog.setMessage("Posting to App ...");


        final String title_val = nPostingTitle.getText().toString().trim();
        final String desc_val = nPostingDesc.getText().toString().trim();

        /*
        check if the boxes are empty
         */

        if (!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri != null){

            nProgressDialog.show();

            StorageReference filepath = nStorageindatabase.child("Blog_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = nDatabase.push();


                    nDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            /*
                            adding information to the database title,description,image,userid,username
                             */

                            newPost.child("title").setValue(title_val);
                            newPost.child("desc").setValue(desc_val);
                            newPost.child("image").setValue(downloadUrl.toString());
                            newPost.child("uid").setValue(nUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        startActivity(new Intent(PostForm.this, RestaurantApp.class));
                                    }

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    nProgressDialog.dismiss();


                }
            });


        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * code for the gallery to get images from device and display
         */
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            
            mImageUri = data.getData();
            
            nSelectedImage.setImageURI(mImageUri);
        }
    }
}
