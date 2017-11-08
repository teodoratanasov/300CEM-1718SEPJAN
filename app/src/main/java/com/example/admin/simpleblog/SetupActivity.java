package com.example.admin.simpleblog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class SetupActivity extends AppCompatActivity {

    private ImageButton mSetupImageBtn;
    private EditText mSetupField;
    private Button mSubmitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
    }
}
