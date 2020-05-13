package com.faisaljaved.myparking.Activities;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.faisaljaved.myparking.BaseActivity;
import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.adapters.AdDataRecyclerAdapter;
import com.faisaljaved.myparking.listener.OnDataClickListener;
import com.faisaljaved.myparking.models.MyAdData;
import com.faisaljaved.myparking.viewmodels.AdDataViewModel;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.List;

public class MyAdsActivity extends BaseActivity implements OnDataClickListener {


    private SpaceNavigationView spaceNavigationView;
    private boolean doubleBackToExitPressedOnce = false;

    //Ui components
    private RecyclerView mRecyclerView;
    private Button mPostAd;
    private ImageView mNoParking;

    private AdDataRecyclerAdapter mAdapter;
    private AdDataViewModel mViewModel;

    //firebase
    private FirebaseUser mUser;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);
        initBottomNavBar(savedInstanceState);

        //firebase
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserId = mUser.getUid();

        mRecyclerView = findViewById(R.id.profile_recycler_view);
        mNoParking = findViewById(R.id.no_parking_ads);
        mPostAd = findViewById(R.id.start_posting_tv);
        mPostAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyAdsActivity.this,PostAdActivity.class);
                startActivity(intent);
            }
        });

        mViewModel = new ViewModelProvider(this).get(AdDataViewModel.class);

        initRecyclerView();
        subscribeObservers();
        mViewModel.loadMyAdDataFromFirebase(mUserId);
    }

    private void subscribeObservers() {
        mViewModel.getMyAdData().observe(this, new Observer<List<MyAdData>>() {
            @Override
            public void onChanged(List<MyAdData> myAdData) {
                if(myAdData != null){
                    if (myAdData.size() == 0){
                        mRecyclerView.setVisibility(View.GONE);
                        mNoParking.setVisibility(View.VISIBLE);
                        mPostAd.setVisibility(View.VISIBLE);
                    }else {
                        mAdapter.setAdData(myAdData);
                        mAdapter.setSwipetoDelete(true);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mNoParking.setVisibility(View.GONE);
                        mPostAd.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void initRecyclerView() {
        mAdapter = new AdDataRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                if (mAdapter.getSelectedAdData(viewHolder.getAdapterPosition()) != null) {
                    new iOSDialogBuilder(MyAdsActivity.this)
                            .setTitle("Delete your Ad?")
                            .setSubtitle("Your Ad will be deleted permanently. Are you sure you want to continue?")
                            .setBoldPositiveLabel(true)
                            .setCancelable(false)
                            .setPositiveListener(getString(R.string.ok),new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                    MyAdData adData = mAdapter.getSelectedAdData(viewHolder.getAdapterPosition());
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("data").child(adData.getAdId());
                                    reference.removeValue();
                                    dialog.dismiss();

                                }
                            })
                            .setNegativeListener(getString(R.string.dismiss), new iOSDialogClickListener() {
                                @Override
                                public void onClick(iOSDialog dialog) {
                                    dialog.dismiss();
                                    mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            })
                            .build().show();

                }
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    private void initBottomNavBar(Bundle savedInstanceState) {

        spaceNavigationView = findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("HOME", R.drawable.ic_home));
        spaceNavigationView.addSpaceItem(new SpaceItem("CHAT", R.drawable.ic_question_answer));
        spaceNavigationView.addSpaceItem(new SpaceItem("ADS", R.drawable.ic_assignment));
        spaceNavigationView.addSpaceItem(new SpaceItem("PROFILE", R.drawable.ic_person));
        spaceNavigationView.changeCurrentItem(2);
        spaceNavigationView.setActiveSpaceItemColor(ContextCompat.getColor(this, R.color.violet));
        spaceNavigationView.showIconOnly();

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent intent = new Intent(MyAdsActivity.this, PostAdActivity.class);
                startActivity(intent);
            }


            @Override
            public void onItemClick(int itemIndex, String itemName) {

                switch (itemIndex) {

                    case 0:
                        Intent goToProfile = new Intent(MyAdsActivity.this, ProfileActivity.class);
                        goToProfile.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(goToProfile);
                        break;
                    case 1:
                        Intent goToChats = new Intent(MyAdsActivity.this, ChatsActivity.class);
                        goToChats.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(goToChats);
                        break;
                    case 3:
                        Intent goToUser = new Intent(MyAdsActivity.this, UserProfileActivity.class);
                        goToUser.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(goToUser);
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
        int flag = getIntent().getFlags();
        if (flag == Intent.FLAG_ACTIVITY_REORDER_TO_FRONT){
            spaceNavigationView.changeCurrentItem(2);
        }
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

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(MyAdsActivity.this, SingleAdActivity.class);
        intent.putExtra("adData", mAdapter.getSelectedAdData(position));
        startActivity(intent);
    }
}
