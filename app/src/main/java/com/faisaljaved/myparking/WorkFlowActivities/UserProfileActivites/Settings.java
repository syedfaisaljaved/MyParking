package com.faisaljaved.myparking.WorkFlowActivities.UserProfileActivites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.faisaljaved.myparking.LoginActivity;
import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.WorkFlowActivities.UserProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Settings extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private TextView mLogout, mDeleteAccount;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initFirebaseAuth();

        mLogout = findViewById(R.id.user_profile_logout);
        mDeleteAccount = findViewById(R.id.user_profile_delete_account);

        mLogout.setOnClickListener(mOnClickListener);
        mDeleteAccount.setOnClickListener(mOnClickListener);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.this, UserProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
            }
        });
    }

    private void initFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.user_profile_logout:
                    firebaseAuth.signOut();
                    Intent intent = new Intent(Settings.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(Settings.this, "Logged Out", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.user_profile_delete_account:
                    currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Settings.this, "Account Deleted Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent2 = new Intent(Settings.this, LoginActivity.class);
                    startActivity(intent2);
                    finish();
                break;
            }
        }
    };

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
