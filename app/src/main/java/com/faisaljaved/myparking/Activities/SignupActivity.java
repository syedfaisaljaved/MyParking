package com.faisaljaved.myparking.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.faisaljaved.myparking.BaseActivity;
import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends BaseActivity {

    private static final String TAG = "SignupActivity";

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private String userId;

    //ui elements
    private TextView mLogin;
    private Button mSignup;
    private TextInputLayout mName, mEmail, mPass, mNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initFirebase();

        mLogin = (TextView) findViewById(R.id.signup_login_button);
        mSignup = (Button) findViewById(R.id.signupscreen_button);
        mName = findViewById(R.id.signup_username_edit_text_view);
        mEmail = findViewById(R.id.signup_email_edit_text_view);
        mPass = findViewById(R.id.signup_password_edit_text_view);
        mNumber = findViewById(R.id.signup_number_edit_text_view);

        mLogin.setOnClickListener(mOnClickListener);
        mSignup.setOnClickListener(mOnClickListener);
    }

    private void initFirebase() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){

                case R.id.signup_login_button:
                    finish();
                    break;

                case R.id.signupscreen_button:

                    final String enteredEmail = mEmail.getEditText().getText().toString().trim();
                    final String enteredPass = mPass.getEditText().getText().toString().trim();
                    final String fullname = mName.getEditText().getText().toString().trim();
                    final String phoneNumber = mNumber.getEditText().getText().toString().trim();

                    if (TextUtils.isEmpty(enteredEmail)){
                        Toast.makeText(getApplicationContext(),"Please enter email",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(enteredPass)){
                        Toast.makeText(getApplicationContext(),"Password is required",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(fullname) && TextUtils.isEmpty(phoneNumber)){
                        Toast.makeText(getApplicationContext(),"Field can't be empty",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    firebaseAuth.createUserWithEmailAndPassword(enteredEmail,enteredPass).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Registered Successfully",Toast.LENGTH_SHORT).show();

                                userId = task.getResult().getUser().getUid();
                                final UserDetails userDetails = new UserDetails(fullname,phoneNumber,enteredEmail,enteredPass,"default");

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        reference.child("userDetails").child(userId).setValue(userDetails);
                                        //back to login activity
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                                //back to login activity
                                finish();

                            }else {
                                Toast.makeText(getApplicationContext(), "Error: "+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                    break;
            }
        }
    };
}
