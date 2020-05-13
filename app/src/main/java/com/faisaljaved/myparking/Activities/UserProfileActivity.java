package com.faisaljaved.myparking.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faisaljaved.myparking.BaseActivity;
import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.models.Images;
import com.faisaljaved.myparking.models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;

public class UserProfileActivity extends BaseActivity {

    private static final String TAG = "UserProfileActivity";

    //bottom navbar
    private SpaceNavigationView spaceNavigationView;

    private boolean doubleBackToExitPressedOnce = false;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userId;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    //ui Components
    private TextView mFullname, mEmail;
    private ImageView mProfileImage, mImageEdit, mClickToEdit, mDoneEdit;
    private RelativeLayout mSettings;
    private EditText mUsernameEdit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initBottomNavBar(savedInstanceState);
        initFirebaseAuth();

        mFullname = (TextView) findViewById(R.id.user_profile_username_profile);
        mEmail = (TextView) findViewById(R.id.user_profile_email_profile);
        mSettings = (RelativeLayout) findViewById(R.id.user_profile_layout_settings);
        mProfileImage = findViewById(R.id.user_profile_image);
        mImageEdit = findViewById(R.id.user_profile_image_edit);
        mUsernameEdit = findViewById(R.id.user_profile_username_edit);
        mClickToEdit = findViewById(R.id.user_profile_edit_details);
        mDoneEdit = findViewById(R.id.user_profile_done_edit);

        showFetchedData();
        String oldImage = mProfileImage.getDrawable().toString();
        Log.d(TAG, "onComplete: image url "+ oldImage);

        mSettings.setOnClickListener(listener);
        mImageEdit.setOnClickListener(listener);
        mClickToEdit.setOnClickListener(listener);
        mDoneEdit.setOnClickListener(listener);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){

                case R.id.user_profile_layout_settings:
                    Intent intent = new Intent(UserProfileActivity.this, Settings.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    break;

                case R.id.user_profile_image_edit:
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(UserProfileActivity.this);
                    break;

                case R.id.user_profile_edit_details:
                    mUsernameEdit.setText(mFullname.getText().toString());
                    mFullname.setVisibility(View.GONE);
                    mUsernameEdit.setVisibility(View.VISIBLE);
                    mClickToEdit.setVisibility(View.GONE);
                    mDoneEdit.setVisibility(View.VISIBLE);
                    break;

                case R.id.user_profile_done_edit:
                    String newName = mUsernameEdit.getText().toString().trim();
                    String oldname = mFullname.getText().toString();
                    if (!newName.isEmpty() && !newName.equals(oldname)){
                        mFullname.setText(newName);
                        updateUsernameFirebase(newName, oldname);
                    }
                    mFullname.setVisibility(View.VISIBLE);
                    mUsernameEdit.setVisibility(View.GONE);
                    mClickToEdit.setVisibility(View.VISIBLE);
                    mDoneEdit.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private void updateUsernameFirebase(final String newName, final String oldname) {
        DatabaseReference postRef = reference;
        postRef.child("username").setValue(newName);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("sellers_buyers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: datasnapshot "+ dataSnapshot);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: postsnapshot "+ postSnapshot);
                    for (DataSnapshot snapshot :postSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: snapshot "+ snapshot);
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: child snapshot "+ childSnapshot);
                            for (DataSnapshot lastSnapshot : childSnapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: lastsnapshot " + lastSnapshot);
                                if (lastSnapshot.child("buyerUsername").getValue().equals(oldname)){
                                    lastSnapshot.child("buyerUsername").getRef().setValue(newName);
                                }
                                if (lastSnapshot.child("sellerUsername").getValue().equals(oldname)){
                                    lastSnapshot.child("sellerUsername").getRef().setValue(newName);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                compressImage(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void compressImage(Uri uri) {
        File file = new File(uri.getPath());
        Luban.compress(this, file)
                .putGear(Luban.THIRD_GEAR)
                .launch(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        uploadImagetoFirebase(file);
                        Glide.with(getApplicationContext())
                                .load(Uri.fromFile(file))
                                .apply(RequestOptions.circleCropTransform())
                                .into(mProfileImage);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }


    private void uploadImagetoFirebase(File file) {
        Images profileImage = new Images(file.getName(),Uri.fromFile(file));
        final StorageReference imageRef = FirebaseStorage.getInstance().getReference().child("profileImages").child(profileImage.getImageName());
        imageRef.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            final String newImage = task.getResult().toString();
                            Log.d(TAG, "onComplete: task " + task.getResult());
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    UserDetails user = dataSnapshot.getValue(UserDetails.class);
                                    String oldImage = user.getImage();
                                    Log.d(TAG, "onDataChange: old image "+ oldImage);
                                    updateImageFirebase(newImage, oldImage);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }
                    });
                }
            }
        });
    }

    private void updateImageFirebase(final String newImage, final String oldImage) {
        // delete old image
        if (!oldImage.equals("default")) {
            final StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImage);
            imageRef.delete();
        }

        reference.child("image").setValue(newImage);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("sellers_buyers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: datasnapshot "+ dataSnapshot);
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: postsnapshot "+ postSnapshot);
                    for (DataSnapshot snapshot :postSnapshot.getChildren()) {
                        Log.d(TAG, "onDataChange: snapshot "+ snapshot);
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: child snapshot "+ childSnapshot);
                            for (DataSnapshot lastSnapshot : childSnapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: lastsnapshot " + lastSnapshot);
                                if (lastSnapshot.child("buyerImage").getValue().equals(oldImage)
                                        && lastSnapshot.child("buyerUsername").getValue().equals(mFullname.getText().toString()) ){
                                    lastSnapshot.child("buyerImage").getRef().setValue(newImage);
                                }
                                if (lastSnapshot.child("sellerImage").getValue().equals(oldImage)
                                        && lastSnapshot.child("sellerUsername").getValue().equals(mFullname.getText().toString())){
                                    lastSnapshot.child("sellerImage").getRef().setValue(newImage);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showFetchedData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails user = new UserDetails();
                user.setUsername(dataSnapshot.getValue(UserDetails.class).getUsername());
                user.setEmail(dataSnapshot.getValue(UserDetails.class).getEmail());
                user.setImage(dataSnapshot.getValue(UserDetails.class).getImage());

                mFullname.setText(user.getUsername());
                mEmail.setText(user.getEmail());
                if (!user.getImage().equals("default")){
                    Glide.with(getApplicationContext())
                            .load(user.getImage())
                            .apply(RequestOptions.circleCropTransform())
                            .into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("userDetails").child(userId);
    }

    private void initBottomNavBar(Bundle savedInstanceState) {
        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("HOME", R.drawable.ic_home));
        spaceNavigationView.addSpaceItem(new SpaceItem("CHAT", R.drawable.ic_question_answer));
        spaceNavigationView.addSpaceItem(new SpaceItem("ADS", R.drawable.ic_assignment));
        spaceNavigationView.addSpaceItem(new SpaceItem("PROFILE", R.drawable.ic_person));
        spaceNavigationView.changeCurrentItem(3);
        spaceNavigationView.setActiveSpaceItemColor(ContextCompat.getColor(this, R.color.violet));
        spaceNavigationView.showIconOnly();

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent intent = new Intent(UserProfileActivity.this, PostAdActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {

                    case 0:
                        Intent goToProfile = new Intent(UserProfileActivity.this, ProfileActivity.class);
                        goToProfile.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(goToProfile);
                        break;
                    case 1:
                        Intent goToChats = new Intent(UserProfileActivity.this, ChatsActivity.class);
                        goToChats.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(goToChats);
                        break;
                    case 2:
                        Intent goToMyAds = new Intent(UserProfileActivity.this, MyAdsActivity.class);
                        goToMyAds.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(goToMyAds);
                        break;
                }
                overridePendingTransition(0,0);

            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        spaceNavigationView.changeCurrentItem(3);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
