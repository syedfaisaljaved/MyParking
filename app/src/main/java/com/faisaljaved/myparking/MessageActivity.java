package com.faisaljaved.myparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.faisaljaved.myparking.adapters.MessageAdapter;
import com.faisaljaved.myparking.adapters.MessageViewHolder;
import com.faisaljaved.myparking.listener.OnDataClickListener;
import com.faisaljaved.myparking.models.ChatUsers;
import com.faisaljaved.myparking.models.Message;
import com.faisaljaved.myparking.models.UserDetails;
import com.faisaljaved.myparking.viewmodels.MessageViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MessageActivity extends AppCompatActivity implements OnDataClickListener {

    private static final String TAG = "MessageActivity";

    //UI components
    private TextView toolbar_text;
    private AppCompatImageView toolbar_image;
    private Toolbar toolbar;
    private EditText type_message;
    private ImageView sendIcon;
    private RelativeLayout sendmask;

    private String sellerUid;
    private String sellerFragmentString;
    private ChatUsers userData;

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
        type_message = findViewById(R.id.edittext_message);
        recyclerView = findViewById(R.id.message_recycler_view);

        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        //textchange listener
        type_message.addTextChangedListener(textChangeListener);

        //onclick listener
        sendmask.setOnClickListener(sendlistener);

        getIncomingIntent();
        initFirebase();
        setToolbar();

        if (sellerUid != null) {
            fetchSetSellerDetails();
        }
        else if (userData != null){
            setUserProperties();
        }

        initRecyclerView();
        subscribeObservers();

        if (sellerFragmentString != null){
            viewModel.loadMessagesFromFirebase(userId, userData.getBuyerUid(),userData.getAdId());
        }
        else {
            viewModel.loadMessagesFromFirebase(userId, userData.getSellerUid(),userData.getAdId());
        }

    }

    private void subscribeObservers() {
        viewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                adapter.setMessages(messages,userId);
            }
        });
    }

    private void initRecyclerView() {
        adapter = new MessageAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
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
            }else {
                sendmask.setBackground(getResources().getDrawable(R.drawable.squircle));
                sendIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_send));
            }


        }
    };

    View.OnClickListener sendlistener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){

                case R.id.send_button_layout:
                    String message = type_message.getText().toString().trim();
                    if (!message.isEmpty()) {
                        sendMessage(message);
                        type_message.getText().clear();
                    }
                    break;
                case R.id.add_icon:

            }

        }
    };

    private void sendMessage(final String message) {
        final DatabaseReference postRef;
        if (sellerFragmentString != null){
            postRef = reference.child("chatMessages").child(userData.getBuyerUid()+userId+userData.getAdId());
        }else {
            postRef = reference.child("chatMessages").child(userId+userData.getSellerUid()+userData.getAdId());
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

        if (sellerFragmentString != null){
            toolbar_text.setText(userData.getBuyerUsername());
        }else {
            toolbar_text.setText(userData.getSellerUsername());
        }

        if (userData.getUserImage().equals("default")){
            toolbar_image.setImageResource(R.drawable.blank_image);
        }else {
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.blank_image);

            Glide.with(getApplicationContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(userData.getUserImage())
                    .apply(RequestOptions.circleCropTransform())
                    .into(toolbar_image);
        }
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getIncomingIntent(){
        sellerUid = getIntent().getStringExtra("uid");
        sellerFragmentString = getIntent().getStringExtra("sellerFragment");
        if (getIntent().hasExtra("userData")) {
            userData = getIntent().getParcelableExtra("userData");
        }
    }

    private void initFirebase() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

    }

    private void fetchSetSellerDetails() {
        final DatabaseReference sellerReference = reference.child("userDetails").child(sellerUid);
        sellerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails sellerDetails = dataSnapshot.getValue(UserDetails.class);

                toolbar_text.setText(sellerDetails.getUsername());

                if (sellerDetails.getImage().equals("default")){
                    toolbar_image.setImageResource(R.drawable.blank_image);
                }else {
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

    }
}
