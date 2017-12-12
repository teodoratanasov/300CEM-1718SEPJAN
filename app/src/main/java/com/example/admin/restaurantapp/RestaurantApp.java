package com.example.admin.restaurantapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RestaurantApp extends AppCompatActivity {
    /**
     * making a recycle view for the
     */
    private RecyclerView nList;

    private DatabaseReference nDatabase;

    private DatabaseReference nDatabaseUsers;

    private FirebaseAuth nAuthentication;
    private FirebaseAuth.AuthStateListener nAuthenticationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantapp);

        nAuthentication = FirebaseAuth.getInstance();
        nAuthenticationListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(RestaurantApp.this, LoginForm.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }
            }

        };

        nDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        nDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        nDatabaseUsers.keepSynced(true);

        nList = (RecyclerView) findViewById(R.id.post_list);
        nList.setHasFixedSize(true);
        nList.setLayoutManager(new LinearLayoutManager(this));

        checkUserExist();



    }

    @Override
    protected void onStart() {
        super.onStart();

        nAuthentication.addAuthStateListener(nAuthenticationListener);

        FirebaseRecyclerAdapter<Posts, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, BlogViewHolder>(
                Posts.class,
                R.layout.posts,
                BlogViewHolder.class,
                nDatabase

        ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, Posts model, int position) {

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());

                viewHolder.nView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Toast.makeText(RestaurantApp.this, "You clicked on the Post", Toast.LENGTH_LONG).show();

                    }
                });

            }
        };

        nList.setAdapter(firebaseRecyclerAdapter);


    }
    /*
    check if the user exists
     */
    private void checkUserExist() {

        if (nAuthentication.getCurrentUser() != null) {

            final String user_id = nAuthentication.getCurrentUser().getUid();

            nDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(user_id)) {

                        Intent setupIntent = new Intent(RestaurantApp.this, RegisterForm.class);
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


    public static class BlogViewHolder extends RecyclerView.ViewHolder{

        View nView;


        public BlogViewHolder(View itemView) {
            super(itemView);

            nView = itemView;
        }
        /*
        displaying the title
         */

        public void setTitle(String title){

            TextView post_title = (TextView) nView.findViewById(R.id.posts_title);
            post_title.setText(title);
        }
        /*
        displaying the description
         */

        public void setDesc(String desc){

            TextView post_desc = (TextView) nView.findViewById(R.id.posts_description);
            post_desc.setText(desc);
        }
        /*
        displaying the user username
         */

        public void setUsername(String username){

            TextView post_username = (TextView) nView.findViewById(R.id.posts_username);
            post_username.setText(username);

        }

        /**
         * Loading the images with picasso
         * @param ctx
         * @param image
         */

        public void setImage(Context ctx, String image){

            ImageView post_image = (ImageView) nView.findViewById(R.id.posts_image);
            Picasso.with(ctx).load(image).into(post_image);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/**
 * redirecting to post a blog after clicking add button
 */
        if(item.getItemId() == R.id.action_add){

            startActivity(new Intent(RestaurantApp.this, PostForm.class));
        }
/**
 * performing log out after clicking the log out button
 */
        if (item.getItemId() == R.id.action_logout){

            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
/**
 * ensuring that the user is singed out
 */
        nAuthentication.signOut();
    }
}

