package com.faisaljaved.myparking.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faisaljaved.myparking.BaseActivity;
import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.adapters.MessageAdapter;
import com.faisaljaved.myparking.listener.OnDataClickListener;
import com.faisaljaved.myparking.models.ChatUsers;
import com.faisaljaved.myparking.models.Images;
import com.faisaljaved.myparking.models.Message;
import com.faisaljaved.myparking.models.MyAdData;
import com.faisaljaved.myparking.models.UserDetails;
import com.faisaljaved.myparking.viewmodels.MessageViewModel;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.List;
import java.util.Objects;

import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;

public class MessageActivity extends BaseActivity implements OnDataClickListener {

    private static final String TAG = "MessageActivity";

    //UI components
    private TextView toolbar_text;
    private AppCompatImageView toolbar_image;
    private Toolbar toolbar;
    private EditText type_message;
    private ImageView sendIcon, addIcon;
    private RelativeLayout sendmask;

    private ProgressDialog progressDialog;

    private String sellerFragmentString;
    private ChatUsers userData;

    private MyAdData adData;
    private boolean isfromSingleActivity;

    private MessageAdapter adapter;
    private MessageViewModel viewModel;
    private RecyclerView recyclerView;

    //firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userId;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        toolbar_text = findViewById(R.id.toolbar_text);
        toolbar_image = findViewById(R.id.toolbar_image);
        sendmask = findViewById(R.id.send_button_layout);
        sendIcon = findViewById(R.id.send_icon);
        addIcon = findViewById(R.id.add_icon);
        type_message = findViewById(R.id.edittext_message);
        recyclerView = findViewById(R.id.message_recycler_view);

        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        //progressDialog
        progressDialog = new ProgressDialog(this);

        //textchange listener
        type_message.addTextChangedListener(textChangeListener);

        //onclick listener
        sendmask.setOnClickListener(sendlistener);
        addIcon.setOnClickListener(sendlistener);

        getIncomingIntent();
        initFirebase();
        setToolbar();

        if (isfromSingleActivity) {

            fetchSetSellerDetails();

            fetchChatUser(new MyCallback() {
                @Override
                public void onCallback(ChatUsers value) {
                    userData = value;
                    showProgressBar(true);
                    initRecyclerView();
                    subscribeObservers();

                    if (sellerFragmentString != null) {
                        viewModel.loadMessagesFromFirebase(userId, userData.getBuyerUid(), userData.getAdId());
                    } else {
                        viewModel.loadMessagesFromFirebase(userId, userData.getSellerUid(), userData.getAdId());
                    }
                }
            });
        }

        if (userData != null && !isfromSingleActivity) {
            setUserProperties();
            initRecyclerView();
            subscribeObservers();

            if (sellerFragmentString != null) {
                viewModel.loadMessagesFromFirebase(userId, userData.getBuyerUid(), userData.getAdId());
            } else {
                viewModel.loadMessagesFromFirebase(userId, userData.getSellerUid(), userData.getAdId());
            }
        }

    }

    private void reAddDeletedUser() {

        if (sellerFragmentString == null) {
            final DatabaseReference postReference = reference.child("sellers_buyers");
            postReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.child(userData.getSellerUid()).child("selling").child(userData.getAdId()).child(userId).exists()) {
                        postReference.child(userData.getSellerUid()).child("selling").child(userData.getAdId()).child(userId).setValue(userData);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            final DatabaseReference postReference = reference.child("sellers_buyers");
            postReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.child(userData.getBuyerUid()).child("buying").child(userId).child(userData.getAdId()).exists()) {
                        postReference.child(userData.getBuyerUid()).child("buying").child(userId).child(userData.getAdId()).setValue(userData);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
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
                        uploadImageMessage(file);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }


    private void uploadImageMessage(File file) {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        StorageReference storeRef = FirebaseStorage.getInstance().getReference();
        final StorageReference postRef;
        Images profileImage = new Images(file.getName(), Uri.fromFile(file));

        if (sellerFragmentString != null) {
            postRef = storeRef.child("chatData").child(userData.getBuyerUid() + userId + userData.getAdId()).child(profileImage.getImageName());
            ;
        } else {
            postRef = storeRef.child("chatData").child(userId + userData.getSellerUid() + userData.getAdId()).child(profileImage.getImageName());
        }
        postRef.putFile(Uri.fromFile(file)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    postRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            sendImage(task.getResult().toString());
                        }
                    });
                }
            }
        });
    }

    private void sendImage(final String message) {
        final DatabaseReference postRef;
        if (sellerFragmentString != null) {
            postRef = reference.child("chatMessages").child(userData.getBuyerUid() + userId + userData.getAdId());
        } else {
            postRef = reference.child("chatMessages").child(userId + userData.getSellerUid() + userData.getAdId());
        }

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String push_key = postRef.push().getKey();
                Message msg = new Message(message, String.valueOf(System.currentTimeMillis()), "image", userId);
                postRef.child(push_key).setValue(msg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void fetchChatUser(final MyCallback myCallback) {

        DatabaseReference buyref = reference.child("sellers_buyers").child(userId).child("buying").child(adData.getUID()).child(adData.getAdId());
        buyref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatUsers data = dataSnapshot.getValue(ChatUsers.class);
                myCallback.onCallback(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void subscribeObservers() {
        viewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                adapter.setMessages(messages, userId);
                progressDialog.dismiss();

                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                        recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                    }
                }, 100);
            }
        });
    }

    private void initRecyclerView() {
        adapter = new MessageAdapter(this);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

    }

    TextWatcher textChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String message = type_message.getText().toString().trim();
            if (!message.isEmpty()) {
                sendmask.setBackground(getResources().getDrawable(R.drawable.squircle_active));
                sendIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_send_active));
            } else {
                sendmask.setBackground(getResources().getDrawable(R.drawable.squircle));
                sendIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_send));
            }


        }
    };

    View.OnClickListener sendlistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.send_button_layout:
                    String message = type_message.getText().toString().trim();
                    if (!message.isEmpty()) {
                        sendMessage(message);
                        type_message.getText().clear();
                        reAddDeletedUser();
                    }
                    break;
                case R.id.add_icon:
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(MessageActivity.this);
                    reAddDeletedUser();
                    break;
            }

        }
    };

    private void sendMessage(final String message) {
        final DatabaseReference postRef;
        if (sellerFragmentString != null) {
            postRef = reference.child("chatMessages").child(userData.getBuyerUid() + userId + userData.getAdId());
        } else {
            postRef = reference.child("chatMessages").child(userId + userData.getSellerUid() + userData.getAdId());
        }

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String push_key = postRef.push().getKey();
                Message msg = new Message(message, String.valueOf(System.currentTimeMillis()), "text", userId);
                postRef.child(push_key).setValue(msg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUserProperties() {

        if (sellerFragmentString != null) {
            toolbar_text.setText(userData.getBuyerUsername());
        } else {
            toolbar_text.setText(userData.getSellerUsername());
        }

        if (sellerFragmentString != null) {
            if (userData.getSellerImage().equals("default")) {
                toolbar_image.setImageResource(R.drawable.blank_image);
            } else {
                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.blank_image);

                Glide.with(getApplicationContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(userData.getBuyerImage())
                        .apply(RequestOptions.circleCropTransform())
                        .into(toolbar_image);
            }

        } else {
            if (userData.getSellerImage().equals("default")) {
                toolbar_image.setImageResource(R.drawable.blank_image);
            } else {
                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.blank_image);

                Glide.with(getApplicationContext())
                        .setDefaultRequestOptions(requestOptions)
                        .load(userData.getSellerImage())
                        .apply(RequestOptions.circleCropTransform())
                        .into(toolbar_image);
            }
        }
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getIncomingIntent() {
        if (getIntent().hasExtra("adDatafromSingleActivity")) {
            adData = getIntent().getParcelableExtra("adDatafromSingleActivity");
            isfromSingleActivity = getIntent().getExtras().getBoolean("isfromSingleActivity");
        }

        sellerFragmentString = getIntent().getStringExtra("sellerFragment");
        if (getIntent().hasExtra("userData")) {
            userData = getIntent().getParcelableExtra("userData");
            Log.d(TAG, "getIncomingIntent: uswrdata " + userData.getBuyerUsername());
        }
    }

    private void initFirebase() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        Log.d(TAG, "initFirebase: userID " + userId);

    }

    private void fetchSetSellerDetails() {
        final DatabaseReference sellerReference = reference.child("userDetails").child(adData.getUID());
        sellerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails sellerDetails = dataSnapshot.getValue(UserDetails.class);

                toolbar_text.setText(sellerDetails.getUsername());

                if (sellerDetails.getImage().equals("default")) {
                    toolbar_image.setImageResource(R.drawable.blank_image);
                } else {
                    RequestOptions requestOptions = new RequestOptions()
                            .placeholder(R.drawable.blank_image);

                    Glide.with(getApplicationContext())
                            .setDefaultRequestOptions(requestOptions)
                            .load(sellerDetails.getImage())
                            .apply(RequestOptions.circleCropTransform())
                            .into(toolbar_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {
        if (adapter.getSelectedMessage(position).getType().equals("image")) {
            imagePopUp(position);
        }

    }

    public void imagePopUp(int position) {
        final Dialog nagDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setContentView(R.layout.preview_image);
        ImageView ivPreview = (ImageView) nagDialog.findViewById(R.id.iv_preview_image);

        Glide.with(this)
                .load(adapter.getSelectedMessage(position).getMessage())
                .into(ivPreview);
        nagDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_menu) {
            deletechat();
            finish();
        }
        return true;
    }

    private void deletechat() {

        final DatabaseReference postReference = reference.child("sellers_buyers");
        if (sellerFragmentString == null) {
            postReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postReference.child(userId).child("buying").child(userData.getSellerUid()).child(userData.getAdId()).removeValue();
                    if (!dataSnapshot.child(userData.getSellerUid()).child("selling").child(userData.getAdId()).child(userId).exists()) {
                        clearchats();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            postReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    postReference.child(userId).child("selling").child(userData.getAdId()).child(userData.getBuyerUid()).removeValue();
                    if (!dataSnapshot.child(userData.getBuyerUid()).child("buying").child(userId).child(userData.getAdId()).exists()) {
                        clearchats();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void clearchats() {
        final DatabaseReference postRef;
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        if (sellerFragmentString != null) {
            postRef = reference.child("chatMessages").child(userData.getBuyerUid() + userId + userData.getAdId());
            storageReference.child("chatData").child(userData.getBuyerUid() + userId + userData.getAdId()).delete();

        } else {
            postRef = reference.child("chatMessages").child(userId + userData.getSellerUid() + userData.getAdId());
            storageReference.child("chatData").child(userId + userData.getSellerUid() + userData.getAdId()).delete();

        }
        postRef.removeValue();
    }

    public interface MyCallback {
        void onCallback(ChatUsers value);
    }
}
