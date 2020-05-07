package com.faisaljaved.myparking.WorkFlowActivities;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.faisaljaved.myparking.BaseActivity;
import com.faisaljaved.myparking.PostAdActivity;
import com.faisaljaved.myparking.R;
import com.faisaljaved.myparking.adapters.ChatAdapter;
import com.google.android.material.tabs.TabLayout;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class ChatsActivity extends BaseActivity {


    private SpaceNavigationView spaceNavigationView;
    private boolean doubleBackToExitPressedOnce = false;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ChatAdapter mChatAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        initBottomNavBar(savedInstanceState);

        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);

        mChatAdapter = new ChatAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mChatAdapter);
        mTabLayout.setupWithViewPager(mViewPager);



    }

    private void initBottomNavBar(Bundle savedInstanceState) {
        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("HOME", R.drawable.ic_home));
        spaceNavigationView.addSpaceItem(new SpaceItem("CHAT", R.drawable.ic_question_answer));
        spaceNavigationView.addSpaceItem(new SpaceItem("ADS", R.drawable.ic_assignment));
        spaceNavigationView.addSpaceItem(new SpaceItem("PROFILE", R.drawable.ic_person));
        spaceNavigationView.changeCurrentItem(1);
        spaceNavigationView.setActiveSpaceItemColor(ContextCompat.getColor(this, R.color.violet));
        spaceNavigationView.showIconOnly();

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent intent = new Intent(ChatsActivity.this, PostAdActivity.class);
                startActivity(intent);
            }


            @Override
            public void onItemClick(int itemIndex, String itemName) {

                switch (itemIndex) {

                    case 0:
                        Intent goToProfile = new Intent(ChatsActivity.this, ProfileActivity.class);
                        startActivity(goToProfile);
                        break;
                    case 2:
                        Intent goToChats = new Intent(ChatsActivity.this, MyAdsActivity.class);
                        startActivity(goToChats);
                        break;
                    case 3:
                        Intent goToMyAds = new Intent(ChatsActivity.this, UserProfileActivity.class);
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
}
