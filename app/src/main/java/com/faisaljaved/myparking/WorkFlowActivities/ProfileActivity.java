package com.faisaljaved.myparking.WorkFlowActivities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.faisaljaved.myparking.BaseActivity;
import com.faisaljaved.myparking.PostAdActivity;
import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.WorkFlowActivities.ProfileActivites.SingleAdActivity;
import com.faisaljaved.myparking.adapters.AdDataRecyclerAdapter;
import com.faisaljaved.myparking.listener.OnDataClickListener;
import com.faisaljaved.myparking.models.MyAdData;
import com.faisaljaved.myparking.viewmodels.AdDataViewModel;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.List;

public class ProfileActivity extends BaseActivity implements OnDataClickListener {


    private static final String TAG = "ProfileActivity";
    private SpaceNavigationView spaceNavigationView;
    private boolean doubleBackToExitPressedOnce = false;

    private RecyclerView mRecyclerView;
    private AdDataRecyclerAdapter mAdDataRecyclerAdapter;
    private AdDataViewModel mAdDataViewModel;

    private ShimmerFrameLayout shimmerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initBottomNavBar(savedInstanceState);

        mRecyclerView = findViewById(R.id.profile_recycler_view);
        mAdDataViewModel = new ViewModelProvider(this).get(AdDataViewModel.class);

        shimmerLayout = findViewById(R.id.shimmer_view);
        shimmerLayout.startShimmer();
        shimmerLayout.setVisibility(View.VISIBLE);

        initRecyclerView();
        subscribeObservers();
        mAdDataViewModel.loadDataFromFirebase();
    }

    public void subscribeObservers(){
        mAdDataViewModel.getAdData().observe(this, new Observer<List<MyAdData>>() {
            @Override
            public void onChanged(List<MyAdData> adData) {
                if (adData != null){
                    Log.d(TAG, "onChanged: adData "+ adData);
                    mAdDataRecyclerAdapter.setAdData(adData);
                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void initRecyclerView() {
        mAdDataRecyclerAdapter = new AdDataRecyclerAdapter(this);
        mRecyclerView.setAdapter(mAdDataRecyclerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initBottomNavBar(Bundle savedInstanceState) {
        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("HOME", R.drawable.ic_home));
        spaceNavigationView.addSpaceItem(new SpaceItem("CHAT", R.drawable.ic_question_answer));
        spaceNavigationView.addSpaceItem(new SpaceItem("ADS", R.drawable.ic_assignment));
        spaceNavigationView.addSpaceItem(new SpaceItem("PROFILE", R.drawable.ic_person));
        spaceNavigationView.changeCurrentItem(0);
        spaceNavigationView.setActiveSpaceItemColor(ContextCompat.getColor(this, R.color.violet));
        spaceNavigationView.showIconOnly();

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent intent = new Intent(ProfileActivity.this, PostAdActivity.class);
                startActivity(intent);
            }


            @Override
            public void onItemClick(int itemIndex, String itemName) {

                switch (itemIndex) {

                    case 1:
                        Intent goToProfile = new Intent(ProfileActivity.this, ChatsActivity.class);
                        startActivity(goToProfile);
                        break;
                    case 2:
                        Intent goToChats = new Intent(ProfileActivity.this, MyAdsActivity.class);
                        startActivity(goToChats);
                        break;
                    case 3:
                        Intent goToMyAds = new Intent(ProfileActivity.this, UserProfileActivity.class);
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
        Log.d(TAG, "onItemClick: click detected");
        Intent intent = new Intent(ProfileActivity.this, SingleAdActivity.class);
        intent.putExtra("adData", mAdDataRecyclerAdapter.getSelectedAdData(position));
        startActivity(intent);
    }
}
