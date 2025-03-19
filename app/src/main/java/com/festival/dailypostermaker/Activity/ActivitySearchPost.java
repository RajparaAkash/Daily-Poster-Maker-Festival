package com.festival.dailypostermaker.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.festival.dailypostermaker.Adapter.AdapterSearchCategoryList;
import com.festival.dailypostermaker.Adapter.AdapterSearchCategoryRecent;
import com.festival.dailypostermaker.Model.CategoryName.CategoryItem;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.databinding.ActivitySearchPostBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class ActivitySearchPost extends ActivityBase {

    private AdapterSearchCategoryList adapterSearchCategoryList;
    private ArrayList<CategoryItem> filteredCategoryList = new ArrayList<>();

    private ActivitySearchPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyStatusBarGradient();
        binding = ActivitySearchPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupAd();
        setupClickListeners();
        setupRecyclerView();
        setupSearchEditText();
        handleBackPress();
    }

    private void setupAd() {
        /*GeneralAdsClass.showBannerAd(this, findViewById(R.id.llBanner));*/
    }

    private void setupClickListeners() {
        binding.cancelTxt.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void setupRecyclerView() {
        filteredCategoryList = new ArrayList<>(ActivityMain.searchCategoryList);

        binding.searchCategoryRv.setLayoutManager(new GridLayoutManager(this, 3));
        adapterSearchCategoryList = new AdapterSearchCategoryList(ActivitySearchPost.this, filteredCategoryList);
        binding.searchCategoryRv.setAdapter(adapterSearchCategoryList);

        ArrayList<CategoryItem> recentSearchList = loadRecentSearches();
        if (!recentSearchList.isEmpty()) {
            binding.recentSearchLay.setVisibility(View.VISIBLE);

            FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);
            flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
            flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
            flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
            binding.recentSearchRv.setLayoutManager(flexboxLayoutManager);

            Collections.reverse(recentSearchList);
            AdapterSearchCategoryRecent recentSearchAdapter = new AdapterSearchCategoryRecent(ActivitySearchPost.this, recentSearchList);
            binding.recentSearchRv.setAdapter(recentSearchAdapter);
        }
    }

    private void setupSearchEditText() {
        binding.searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    String searchText = binding.searchEt.getText().toString().trim();

                    if (searchText.isEmpty()) {
                        binding.suggestedLay.setVisibility(View.GONE);
                    } else {
                        filterCategories(searchText);
                    }
                    MyUtil.hideKeyBoard(ActivitySearchPost.this, binding.searchEt);

                    return true;
                }
                return false;
            }
        });

        binding.searchEt.requestFocus();
    }

    private void filterCategories(String searchText) {
        filteredCategoryList.clear();
        for (CategoryItem category : ActivityMain.searchCategoryList) {
            if (category.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredCategoryList.add(category);
            }
        }

        if (filteredCategoryList.isEmpty()) {
            binding.suggestedLay.setVisibility(View.GONE);
        } else {
            binding.suggestedLay.setVisibility(View.VISIBLE);
            adapterSearchCategoryList.notifyDataSetChanged();
        }
    }

    private ArrayList<CategoryItem> loadRecentSearches() {
        ArrayList<CategoryItem> recentSearches = new ArrayList<>();
        SharedPreferences sharedPreferences = getSharedPreferences("SearchPreferences", Context.MODE_PRIVATE);
        String categoryJson = sharedPreferences.getString("RecentCategories", "[]");
        try {
            JSONArray jsonArray = new JSONArray(categoryJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CategoryItem category = new CategoryItem();
                category.setId(jsonObject.getInt("id"));
                category.setName(jsonObject.getString("name"));
                recentSearches.add(category);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recentSearches;
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