package com.faisaljaved.myparking.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.faisaljaved.myparking.BaseActivity;
import com.faisaljaved.myparking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends BaseActivity {

    private Button mLogin;
    private TextView mSignup, mForgetPass;
    private TextInputLayout mEmail, mPass;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        mLogin = (Button) findViewById(R.id.login_button);
        mSignup = (TextView) findViewById(R.id.signup_button1);
        mForgetPass = (TextView) findViewById(R.id.forgot_password_text_view);
        mEmail = findViewById(R.id.email_edit_text_view);
        mPass = findViewById(R.id.password_edit_text_view);

        mLogin.setOnClickListener(mOnClickListener);
        mSignup.setOnClickListener(mOnClickListener);
        mForgetPass.setOnClickListener(mOnClickListener);

    }

    TextView.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()){

                case R.id.login_button:
                    String enteredEmail = mEmail.getEditText().getText().toString().trim();
                    String enteredPass = mPass.getEditText().getText().toString().trim();

                    if (TextUtils.isEmpty(enteredEmail)){
                        Toast.makeText(getApplicationContext(),"Enter Email Id",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(enteredPass)){
                        Toast.makeText(getApplicationContext(),"Enter Password",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    firebaseAuth.signInWithEmailAndPassword(enteredEmail,enteredPass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                /**
                                 * Hide the keyboard after clicking login button.
                                 * Avoids the keyboard from appearing onto the next Fragment.
                                 */
                                InputMethodManager inputManager = (InputMethodManager)
                                        getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);

                                if (inputManager != null) {
                                    inputManager.hideSoftInputFromWindow(LoginActivity.this.getCurrentFocus().getWindowToken(),
                                            InputMethodManager.HIDE_NOT_ALWAYS);
                                }

                                /**
                                 * User is loggged in.
                                 * ProfileActivity Fragment class is replaced with LoginActivity Fragment class.
                                 */

                                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            else {
                                Toast.makeText(getApplicationContext(),"This email is not registered.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;

                case R.id.signup_button1:

                    Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                    startActivity(intent);
                    break;

                case R.id.forgot_password_text_view:

                    startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                    break;

            }
        }
    };
}
