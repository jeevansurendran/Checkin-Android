package com.checkin.app.checkin.AppDetails;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.checkin.app.checkin.Misc.BaseActivity;
import com.checkin.app.checkin.Misc.BlankFragment;
import com.checkin.app.checkin.R;
import com.google.android.material.tabs.TabLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutAppActivity extends BaseActivity {
    @BindView(R.id.tabs_about_license)
    TabLayout tabLayout;
    @BindView(R.id.pager_about_app)
    ViewPager vPager;
    private AboutAppFragment aboutAppFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ButterKnife.bind(this);
        setupUI();
    }

    private void setupUI(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_grey);
        actionBar.setElevation(10);

        vPager.setAdapter(new AppDetailsFragmentAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(vPager);
/*
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==1){
                    openLicenseDetails();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });*/

    }

    private void openLicenseDetails(){
//        startActivity(new Intent(this, OssLicensesMenuActivity.class));

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public class AppDetailsFragmentAdapter extends FragmentPagerAdapter {

        public AppDetailsFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return AboutAppFragment.newInstance();
                case 1:
                    return LicenseAppFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "About Us";
                case 1:
                    return "License";
            }
            return null;
        }
    }

}
