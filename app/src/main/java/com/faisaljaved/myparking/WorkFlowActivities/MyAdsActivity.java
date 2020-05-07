package com.faisaljaved.myparking.WorkFlowActivities;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import com.faisaljaved.myparking.PostAdActivity;
import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.WorkFlowActivities.ProfileActivites.SingleAdActivity;
import com.faisaljaved.myparking.adapters.AdDataRecyclerAdapter;
import com.faisaljaved.myparking.listener.OnDataClickListener;
import com.faisaljaved.myparking.models.MyAdData;
import com.faisaljaved.myparking.viewmodels.AdDataViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
                        startActivity(goToProfile);
                        break;
                    case 1:
                        Intent goToChats = new Intent(MyAdsActivity.this, ChatsActivity.class);
                        startActivity(goToChats);
                        break;
                    case 3:
                        Intent goToMyAds = new Intent(MyAdsActivity.this, UserProfileActivity.class);
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
