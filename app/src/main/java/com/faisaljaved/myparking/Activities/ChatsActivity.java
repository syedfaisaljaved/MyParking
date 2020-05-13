package com.faisaljaved.myparking.Activities;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.faisaljaved.myparking.BaseActivity;
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
                        goToProfile.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(goToProfile);
                        break;
                    case 2:
                        Intent goToMyAds = new Intent(ChatsActivity.this, MyAdsActivity.class);
                        goToMyAds.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(goToMyAds);
                        break;
                    case 3:
                        Intent goToUser = new Intent(ChatsActivity.this, UserProfileActivity.class);
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
            spaceNavigationView.changeCurrentItem(1);
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
}
