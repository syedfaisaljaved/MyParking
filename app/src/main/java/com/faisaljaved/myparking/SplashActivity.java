package com.faisaljaved.myparking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.faisaljaved.myparking.WorkFlowActivities.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                if (currentUser != null){

                    Intent intent = new Intent(SplashActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {

                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 1000);
    }


}