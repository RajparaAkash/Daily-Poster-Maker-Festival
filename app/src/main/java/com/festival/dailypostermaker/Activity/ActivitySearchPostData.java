package com.festival.dailypostermaker.Activity;

import static com.festival.dailypostermaker.Activity.ActivityMain.templateMainLayoutListBusinesses;
import static com.festival.dailypostermaker.Activity.ActivityMain.templateMainLayoutListPersonal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Adapter.AdapterPost;
import com.festival.dailypostermaker.Api.ApiEndpoints;
import com.festival.dailypostermaker.BuildConfig;
import com.festival.dailypostermaker.Model.CategoryName.PostItem;
import com.festival.dailypostermaker.Model.CategoryWiseData.CategoryWiseData;
import com.festival.dailypostermaker.MyApplication;
import com.festival.dailypostermaker.MyUtils.LinearLayoutManagerScroller;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.MyUtils.RvScrollListener;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivitySearchPostDataBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivitySearchPostData extends ActivityBase {

    private int category_Id;
    private String category_name;
    public int totalCategoryPage;
    public int totalCategoryData;

    private AdapterPost adapterPost;
    private ApiEndpoints apiEndpoints;

    private ArrayList<PostItem> bannerPostList;
    private ArrayList<PostItem> bannerPostListWithAds;

    private ActivitySearchPostDataBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchPostDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
            category_Id = intent.getIntExtra("CAT_ID", 641);
            category_name = intent.getStringExtra("CAT_NAME");
        }

        setupAd();
        setupClickListeners();
        handleBackPress();

        binding.headerTxt.setText(category_name);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyUtil.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiEndpoints = retrofit.create(ApiEndpoints.class);

        setupSearchData();

        // Attach the PagerSnapHelper
        /*PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(searchDataPostRv);*/
    }

    private void setupAd() {
        MyAdsManager.displayAdaptiveBannerAdCollapsable(this, findViewById(R.id.llBanner));
    }

    private void setupClickListeners() {
        binding.backLay.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    private void setupSearchData() {
        binding.errorTxt.setVisibility(View.GONE);
        binding.searchDataPostRv.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        Call<CategoryWiseData> call = apiEndpoints.getCategoryWiseData(category_Id, MyApplication.getAppDecryptKey(), BuildConfig.VERSION_CODE, 1);
        call.enqueue(new Callback<CategoryWiseData>() {
            @Override
            public void onResponse(@NonNull Call<CategoryWiseData> call, @NonNull Response<CategoryWiseData> response) {
                if (response.isSuccessful()) {

                    if (response.body().getData() != null) {
                        bannerPostList = new ArrayList<>(response.body().getData().getPost());
                        bannerPostListWithAds = getBannerPostListWithAds(bannerPostList, 3);
                        totalCategoryPage = response.body().getData().getPagination().getTotalPages();
                        totalCategoryData = response.body().getData().getPagination().getTotalItems();

                        setSearchAdapter();
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.searchDataPostRv.setVisibility(View.GONE);
                        binding.errorTxt.setVisibility(View.VISIBLE);
                    }

                } else {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.searchDataPostRv.setVisibility(View.GONE);
                    binding.errorTxt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryWiseData> call, @NonNull Throwable t) {
                binding.progressBar.setVisibility(View.GONE);
                binding.searchDataPostRv.setVisibility(View.GONE);
                binding.errorTxt.setVisibility(View.VISIBLE);
            }
        });
    }

    private ArrayList<PostItem> getBannerPostListWithAds(ArrayList<PostItem> list, int count) {
        ArrayList<PostItem> arrayList = new ArrayList<>();
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (count != 0) {
                    if (i != 0 && i % count == 0) {
                        arrayList.add(null);
                    }
                }
                arrayList.add(list.get(i));
            }
        }
        return arrayList;
    }

    private void setSearchAdapter() {
        binding.progressBar.setVisibility(View.GONE);
        binding.searchDataPostRv.setVisibility(View.VISIBLE);

        if (MyPreference.get_IsProfileBusiness()) {
            adapterPost = new AdapterPost(ActivitySearchPostData.this, bannerPostList, bannerPostListWithAds, templateMainLayoutListBusinesses, totalCategoryData);
        } else {
            adapterPost = new AdapterPost(ActivitySearchPostData.this, bannerPostList, bannerPostListWithAds, templateMainLayoutListPersonal, totalCategoryData);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManagerScroller(this);
        binding.searchDataPostRv.setLayoutManager(linearLayoutManager);
        binding.searchDataPostRv.setNestedScrollingEnabled(false);
        binding.searchDataPostRv.setFitsSystemWindows(true);

        binding.searchDataPostRv.setAdapter(adapterPost);

        binding.searchDataPostRv.addOnScrollListener(new RvScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int i) {
                if (totalCategoryPage >= i) {
                    getMoreData(category_Id, i);
                }
            }
        });
    }

    private void getMoreData(int cateID, int page) {
        binding.progressBarBottom.setVisibility(View.VISIBLE);

        Call<CategoryWiseData> call = apiEndpoints.getCategoryWiseData(cateID, MyApplication.getAppDecryptKey(), BuildConfig.VERSION_CODE, page);
        call.enqueue(new Callback<CategoryWiseData>() {
            @Override
            public void onResponse(@NonNull Call<CategoryWiseData> call, @NonNull Response<CategoryWiseData> response) {
                if (response.isSuccessful()) {
                    binding.progressBarBottom.setVisibility(View.GONE);

                    ArrayList<PostItem> templist = (ArrayList<PostItem>) response.body().getData().getPost();
                    ArrayList<PostItem> templistAds = getBannerPostListWithAdsPagination(templist, 3, bannerPostList.size());

                    bannerPostList.addAll(templist);
                    bannerPostListWithAds.addAll(templistAds);
                    adapterPost.notifyDataSetChanged();

                } else {
                    binding.progressBarBottom.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryWiseData> call, @NonNull Throwable t) {
                binding.progressBarBottom.setVisibility(View.GONE);
            }
        });
    }

    private ArrayList<PostItem> getBannerPostListWithAdsPagination(ArrayList<PostItem> list, int count, int position) {
        ArrayList<PostItem> arrayList = new ArrayList<>();
        if (!list.isEmpty()) {
            for (int i = position; i < (list.size() + position); i++) {
                if (count != 0) {
                    if (i != 0 && i % count == 0) {
                        arrayList.add(null);
                    }
                }
                arrayList.add(list.get(i - position));
            }
        }
        return arrayList;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (MyUtil.isAlternative) {
            setSearchAdapter();
            return;
        }
        if (MyUtil.isUpdate && adapterPost != null) {
            adapterPost.notifyDataSetChanged();
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