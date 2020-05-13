package com.faisaljaved.myparking.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.faisaljaved.myparking.BaseActivity;
import com.faisaljaved.myparking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends BaseActivity {

    private static final String TAG = "ForgotPasswordActivity";

    private TextInputLayout mLostEmailId;
    private Button mSend;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        mLostEmailId = findViewById(R.id.forgotten_email);

        mSend = (Button) findViewById(R.id.send_forgotten_email);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendEmail = mLostEmailId.getEditText().getText().toString().trim();
                firebaseAuth.sendPasswordResetEmail(sendEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this, "Password link sent to mail", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(ForgotPasswordActivity.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });
    }

}
