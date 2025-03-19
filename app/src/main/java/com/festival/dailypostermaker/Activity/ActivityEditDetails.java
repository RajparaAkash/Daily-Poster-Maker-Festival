package com.festival.dailypostermaker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.Fragment.FragmentProfileBusiness;
import com.festival.dailypostermaker.Fragment.FragmentProfilePersonal;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivityEditDetailsBinding;

public class ActivityEditDetails extends ActivityBase {

    private FragmentProfilePersonal fragmentProfilePersonal;
    private FragmentProfileBusiness fragmentProfileBusiness;
    private Fragment currentFragment;

    private ActivityEditDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupAd();
        setupClickListeners();
        setupFragment();
        setupTabLayout();
        handleBackPress();
    }

    private void setupAd() {
        MyAdsManager.displayAdaptiveBannerAd(this, findViewById(R.id.llBanner));
    }

    private void setupFragment() {
        fragmentProfilePersonal = new FragmentProfilePersonal();
        fragmentProfileBusiness = new FragmentProfileBusiness();

        createTabIcons();

        if (MyPreference.get_IsProfileBusiness()) {
            showFragment(fragmentProfileBusiness);
        } else {
            showFragment(fragmentProfilePersonal);
        }
    }

    private void createTabIcons() {
        String[] titleArr = {
                getResources().getString(R.string.personal),
                getResources().getString(R.string.business)};

        for (int i = 0; i < titleArr.length; i++) {
            View tabView = LayoutInflater.from(this).inflate(R.layout.layout_custom_tab_edit_profile, null);

            LinearLayout tabLy = tabView.findViewById(R.id.tabLy);
            TextView tabTxt = tabView.findViewById(R.id.tabTxt);
            tabTxt.setText(titleArr[i]);
            AppCompatImageView tabImg = tabView.findViewById(R.id.tabImg);
            // Set your tab icons here
            switch (i) {
                case 0:
                    tabImg.setImageResource(R.drawable.icon_tab_img_1);
                    break;
                case 1:
                    tabImg.setImageResource(R.drawable.icon_tab_img_2);
                    break;
            }

            // Set initial colors
            if (i == 0) {
                tabTxt.setTextColor(ContextCompat.getColor(this, R.color.white));
                tabImg.setColorFilter(ContextCompat.getColor(this, R.color.white));
                tabLy.setBackgroundResource(R.drawable.bg_tab_selected);
            } else {
                tabTxt.setTextColor(ContextCompat.getColor(this, R.color.tabEditUnselectColor));
                tabImg.setColorFilter(ContextCompat.getColor(this, R.color.tabEditUnselectColor));
                tabLy.setBackgroundResource(R.drawable.bg_tab_unselected);
            }

            binding.tabLayout.addTab(binding.tabLayout.newTab().setCustomView(tabView));
        }
    }

    private void showFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {
            transaction.add(R.id.fragment_container, fragment);
        }
        if (currentFragment != null && currentFragment != fragment) {
            transaction.hide(currentFragment);
        }
        transaction.show(fragment);
        transaction.setPrimaryNavigationFragment(fragment);
        transaction.setReorderingAllowed(true);
        transaction.commitNow();

        currentFragment = fragment;
    }

    private void setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        showFragment(fragmentProfilePersonal);
                        break;
                    case 1:
                        showFragment(fragmentProfileBusiness);
                        break;
                }
                updateTabColors(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabColors(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (MyPreference.get_IsProfileBusiness()) {
            binding.tabLayout.getTabAt(1).select();
        }

        int leftMargin = -30; // Margin for the first tab item from the left
        int rightMargin = -30; // Margin for the second tab item from the right

        ViewGroup slidingTabStrip = (ViewGroup) binding.tabLayout.getChildAt(0);

        View firstTab = slidingTabStrip.getChildAt(0);
        ViewGroup.MarginLayoutParams firstTabParams = (ViewGroup.MarginLayoutParams) firstTab.getLayoutParams();
        firstTabParams.leftMargin = leftMargin;
        firstTabParams.rightMargin = rightMargin;

        View secondTab = slidingTabStrip.getChildAt(1);
        ViewGroup.MarginLayoutParams secondTabParams = (ViewGroup.MarginLayoutParams) secondTab.getLayoutParams();
        secondTabParams.leftMargin = leftMargin;
        secondTabParams.rightMargin = rightMargin;

        /*for (int i = 0; i < editProfileTabLayout.getTabCount(); i++) {
            editProfileTabLayout.getTabAt(i).view.setClickable(false);
        }*/
    }

    private void setupClickListeners() {
        binding.backLay.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.settingImg.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                MyAdsManager.displayInterstitialSecondWise(ActivityEditDetails.this, () -> {
                    startActivity(new Intent(ActivityEditDetails.this, ActivitySettings.class));
                });
            }
        });

        binding.proTxt.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                /*Intent intent = new Intent(EditProfileActivity.this, UpgradeNowFromDashboardActivity.class);
                startActivity(intent);*/
            }
        });
    }

    private void updateTabColors(TabLayout.Tab tab, boolean selected) {
        View tabView = tab.getCustomView();
        TextView tabTxt = tabView.findViewById(R.id.tabTxt);
        ImageView tabImg = tabView.findViewById(R.id.tabImg);
        LinearLayout tabLy = tabView.findViewById(R.id.tabLy);

        if (selected) {
            tabTxt.setTextColor(ContextCompat.getColor(this, R.color.white));
            tabImg.setColorFilter(ContextCompat.getColor(this, R.color.white));
            tabLy.setBackgroundResource(R.drawable.bg_tab_selected);
        } else {
            tabTxt.setTextColor(ContextCompat.getColor(this, R.color.tabEditUnselectColor));
            tabImg.setColorFilter(ContextCompat.getColor(this, R.color.tabEditUnselectColor));
            tabLy.setBackgroundResource(R.drawable.bg_tab_unselected);
        }
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }
}