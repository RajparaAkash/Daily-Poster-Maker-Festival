package com.festival.dailypostermaker.Activity;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Adapter.AdapterCategoryList;
import com.festival.dailypostermaker.Adapter.AdapterPost;
import com.festival.dailypostermaker.Api.RetrofitClient;
import com.festival.dailypostermaker.Api.ApiEndpoints;
import com.festival.dailypostermaker.BuildConfig;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.Model.CategoryName.CategoryItem;
import com.festival.dailypostermaker.Model.CategoryName.CategoryName;
import com.festival.dailypostermaker.Model.CategoryName.PostItem;
import com.festival.dailypostermaker.Model.CategoryWiseData.CategoryWiseData;
import com.festival.dailypostermaker.Model.PostDetails;
import com.festival.dailypostermaker.Model.TemplateMainLayout;
import com.festival.dailypostermaker.MyApplication;
import com.festival.dailypostermaker.MyUtils.DialogUtil;
import com.festival.dailypostermaker.MyUtils.InAppUpdate;
import com.festival.dailypostermaker.MyUtils.LinearLayoutManagerScroller;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.MyUtils.RvScrollListener;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivityMainBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityMain extends ActivityBase {

    private static final String TAG = ActivityMain.class.getSimpleName();

    private AdapterCategoryList adapterCategoryList;
    private AdapterPost adapterPost;
    LinearLayoutManager linearLayoutManager;

    public static ArrayList<TemplateMainLayout> templateMainLayoutListBusinesses = new ArrayList<>();
    public static ArrayList<TemplateMainLayout> templateMainLayoutListPersonal = new ArrayList<>();

    private ArrayList<PostDetails> framePersonalList1 = new ArrayList<>();
    private ArrayList<PostDetails> framePersonalList2 = new ArrayList<>();
    private ArrayList<PostDetails> frameBusinessList1 = new ArrayList<>();
    private ArrayList<PostDetails> frameBusinessList2 = new ArrayList<>();

    ArrayList<CategoryItem> categoryList = new ArrayList<>();
    ArrayList<PostItem> bannerPostList = new ArrayList<>();
    ArrayList<PostItem> bannerPostListWithAds = new ArrayList<>();
    public static ArrayList<CategoryItem> searchCategoryList = new ArrayList<>();

    public int totalCategoryPage;
    public int totalCategoryData;
    private ApiEndpoints apiEndpoints;

    private InAppUpdate inAppUpdate;

    private String templateId = "0";

    private boolean callMoreData = false;

    private RvScrollListener rvScrollListener1 = null;
    private RvScrollListener rvScrollListener2 = null;

    private String tab = "Daily";
    private int postType = 0;
    private int languagesId = 0;

    private long BackPressedTime;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyStatusBarGradient();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (deleteFolder(new File(getCacheDir(), "TEMP"))) {
            Log.d("TAG", "Temp Folder Delete Successfully");
        }

        if (!new File(getCacheDir(), "TEMP").exists()) {
            new File(getCacheDir(), "TEMP").mkdirs();
        }

        inAppUpdate = new InAppUpdate(ActivityMain.this);
        inAppUpdate.checkForAppUpdate();

        try {
            if (getIntent() != null) {
                templateId = getIntent().getStringExtra("TemplateId");
                postType = Integer.parseInt(getIntent().getStringExtra("PostType"));
            } else {
                templateId = "0";
                postType = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            templateId = "0";
            postType = 0;
        }

        if (!checkNotificationPermissionGranted()) {
            new Handler().postDelayed(this::takeNotificationPermission, 500);
        }

        if (MyPreference.get_IsProfileBusiness()) {
            binding.changeProfileImg.setImageResource(R.drawable.icon_main_screen_personal_profile);
        } else {
            binding.changeProfileImg.setImageResource(R.drawable.icon_main_business_profile);
        }

        binding.searchLay.setEnabled(false);
        binding.changeProfileLay.setEnabled(false);

        setupClickListeners();
        changeProfilePicture();

        framePersonalList1.add(new PostDetails(R.layout.frame_personal_1_1));
        framePersonalList1.add(new PostDetails(R.layout.frame_personal_1_2));
        framePersonalList1.add(new PostDetails(R.layout.frame_personal_1_3));
        framePersonalList1.add(new PostDetails(R.layout.frame_personal_1_4));

        framePersonalList2.add(new PostDetails(R.layout.frame_personal_2_1));
        framePersonalList2.add(new PostDetails(R.layout.frame_personal_2_2));

        frameBusinessList1.add(new PostDetails(R.layout.frame_business_1_1));
        frameBusinessList1.add(new PostDetails(R.layout.frame_business_1_2));
        frameBusinessList1.add(new PostDetails(R.layout.frame_business_1_3));
        frameBusinessList1.add(new PostDetails(R.layout.frame_business_1_4));

        frameBusinessList2.add(new PostDetails(R.layout.frame_business_2_1));
        frameBusinessList2.add(new PostDetails(R.layout.frame_business_2_2));

        templateMainLayoutListPersonal = new ArrayList<>();
        templateMainLayoutListPersonal.add(new TemplateMainLayout(R.layout.item_poster_1, framePersonalList1));
        templateMainLayoutListPersonal.add(new TemplateMainLayout(R.layout.item_poster_1, framePersonalList2));

        templateMainLayoutListBusinesses = new ArrayList<>();
        templateMainLayoutListBusinesses.add(new TemplateMainLayout(R.layout.item_poster_2, frameBusinessList1));
        templateMainLayoutListBusinesses.add(new TemplateMainLayout(R.layout.item_poster_1, frameBusinessList2));

        apiEndpoints = RetrofitClient.getInstance().create(ApiEndpoints.class);

        if (postType == 0) {
            tab = "Daily";
            binding.dbViewFestival.setVisibility(View.GONE);
            binding.dbViewQuotes.setVisibility(View.VISIBLE);

            binding.dbImgQuotes.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tabSelectColor)));
            binding.dbTxtQuotes.setTextColor(ContextCompat.getColor(this, R.color.tabSelectColor));

            binding.dbImgFestival.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tabUnSelectColor)));
            binding.dbTxtFestival.setTextColor(ContextCompat.getColor(this, R.color.tabUnSelectColor));
        } else {
            tab = "Festival";
            binding.dbViewQuotes.setVisibility(View.GONE);
            binding.dbViewFestival.setVisibility(View.VISIBLE);

            binding.dbImgFestival.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tabSelectColor)));
            binding.dbTxtFestival.setTextColor(ContextCompat.getColor(this, R.color.tabSelectColor));

            binding.dbImgQuotes.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.tabUnSelectColor)));
            binding.dbTxtQuotes.setTextColor(ContextCompat.getColor(this, R.color.tabUnSelectColor));
        }

        getDataFromApi();

        // Attach the PagerSnapHelper
        /*PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mainTempRv);*/

        handleBackPress();

        FirebaseMessaging.getInstance().subscribeToTopic("all").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e(TAG, "Subscribed to topic");
            }
        });
    }

    private void setupClickListeners() {
        binding.dbLayQuotes.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (tab.equalsIgnoreCase("Daily")) {
                    return;
                }
                postType = 0;
                tab = "Daily";
                if (!templateId.equalsIgnoreCase("0")) {
                    templateId = "0";
                }
                getDataFromApi();
                binding.dbViewFestival.setVisibility(View.GONE);
                binding.dbViewQuotes.setVisibility(View.VISIBLE);

                binding.dbImgQuotes.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor)));
                binding.dbTxtQuotes.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor));

                binding.dbImgFestival.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor)));
                binding.dbTxtFestival.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            }
        });

        binding.dbLayFestival.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (tab.equalsIgnoreCase("Festival")) {
                    return;
                }
                postType = 1;
                tab = "Festival";
                if (!templateId.equalsIgnoreCase("0")) {
                    templateId = "0";
                }
                getDataFromApi();
                binding.dbViewQuotes.setVisibility(View.GONE);
                binding.dbViewFestival.setVisibility(View.VISIBLE);

                binding.dbImgFestival.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor)));
                binding.dbTxtFestival.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor));

                binding.dbImgQuotes.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor)));
                binding.dbTxtQuotes.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            }
        });

        binding.dbLayCreate.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    Dexter.withContext(ActivityMain.this).withPermissions(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                        createLayClick();
                                    }
                                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                        showSettingsDialog();
                                    }
                                }

                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).withErrorListener(new PermissionRequestErrorListener() {
                                public void onError(DexterError dexterError) {
                                    Toast.makeText(ActivityMain.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                                }
                            }).onSameThread().check();
                } else {
                    createLayClick();
                }
            }

            public void createLayClick() {
                MyAdsManager.displayInterstitialSecondWise(ActivityMain.this, () -> {
                    startActivity(new Intent(ActivityMain.this, ActivityCreatePersonalPost.class));
                });
            }
        });

        binding.dbLayDownload.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    Dexter.withContext(ActivityMain.this).withPermissions(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                        downloadLayClick();
                                    }
                                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                        showSettingsDialog();
                                    }
                                }

                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).withErrorListener(new PermissionRequestErrorListener() {
                                public void onError(DexterError dexterError) {
                                    Toast.makeText(ActivityMain.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                                }
                            }).onSameThread().check();
                } else {
                    downloadLayClick();
                }
            }

            public void downloadLayClick() {
                int fileCount = 0;
                try {
                    final File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + getResources().getString(R.string.app_name));
                    if (folder.exists() && folder.isDirectory()) {
                        fileCount = Objects.requireNonNull(folder.listFiles()).length;
                    } else {
                        folder.mkdirs();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (fileCount > 0) {
                    MyAdsManager.displayInterstitialSecondWise(ActivityMain.this, () -> {
                        startActivity(new Intent(ActivityMain.this, ActivityDownloads.class));
                    });
                } else {
                    startActivity(new Intent(ActivityMain.this, ActivityDownloads.class));
                }
            }
        });

        binding.langPostImg.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                dialogLanguage(view);
            }
        });

        binding.searchLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    Dexter.withContext(ActivityMain.this).withPermissions(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                        editLayClick();
                                    }
                                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                        showSettingsDialog();
                                    }
                                }

                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).withErrorListener(new PermissionRequestErrorListener() {
                                public void onError(DexterError dexterError) {
                                    Toast.makeText(ActivityMain.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                                }
                            }).onSameThread().check();
                } else {
                    editLayClick();
                }
            }

            public void editLayClick() {
                MyAdsManager.displayInterstitialSecondWise(ActivityMain.this, () -> {
                    startActivity(new Intent(ActivityMain.this, ActivitySearchPost.class));
                });
            }
        });

        binding.changeProfileLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (MyPreference.get_IsProfileBusiness()) {
                    MyPreference.set_IsProfileBusiness(false);
                } else {
                    MyPreference.set_IsProfileBusiness(true);
                }
                changeProfile();
            }
        });

        binding.profileLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    Dexter.withContext(ActivityMain.this).withPermissions(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .withListener(new MultiplePermissionsListener() {
                                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                        profileClick();
                                    }
                                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                                        showSettingsDialog();
                                    }
                                }

                                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).withErrorListener(new PermissionRequestErrorListener() {
                                public void onError(DexterError dexterError) {
                                    Toast.makeText(ActivityMain.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                                }
                            }).onSameThread().check();
                } else {
                    profileClick();
                }
            }

            public void profileClick() {
                MyAdsManager.displayInterstitialSecondWise(ActivityMain.this, () -> {
                    startActivity(new Intent(ActivityMain.this, ActivityEditDetails.class));
                });
            }
        });

        binding.shareAppLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                shareApp();
            }
        });

        binding.tryAgainTxt.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                getDataFromApi();
            }
        });
    }

    private void dialogLanguage(View view1) {
        PopupWindow popupWindow = new PopupWindow(ActivityMain.this);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        LayoutInflater inflater = LayoutInflater.from(ActivityMain.this);
        View contentView = inflater.inflate(R.layout.dialog_language, null);
        contentView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        popupWindow.setContentView(contentView);
        popupWindow.setBackgroundDrawable(null);
        // Absolute location of the v view
        int[] location = new int[2];
        view1.getLocationOnScreen(location);

        int width = popupWindow.getContentView().getMeasuredWidth();
        int height = popupWindow.getContentView().getMeasuredHeight();

        popupWindow.showAtLocation(view1, Gravity.NO_GRAVITY, location[0] - (width) / 2, location[1] + (view1.getHeight() - 10));

        AppCompatTextView tvAll = contentView.findViewById(R.id.tvAll);
        AppCompatTextView tvEnglish = contentView.findViewById(R.id.tvEnglish);
        AppCompatTextView tvHindi = contentView.findViewById(R.id.tvHindi);
        AppCompatTextView tvMarathi = contentView.findViewById(R.id.tvMarathi);
        AppCompatTextView tvGujarati = contentView.findViewById(R.id.tvGujarati);

        if (languagesId == 13) {
            tvEnglish.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor));
        } else if (languagesId == 14) {
            tvHindi.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor));
        } else if (languagesId == 15) {
            tvGujarati.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor));
        } else if (languagesId == 16) {
            tvMarathi.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor));
        } else {
            tvAll.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor));
        }

        tvAll.setOnClickListener(view -> {
            languagesId = 0;
            tvAll.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor));
            tvEnglish.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvHindi.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvGujarati.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvMarathi.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            popupWindow.dismiss();
            getCategoryWiseData(Integer.parseInt(templateId));
        });

        tvEnglish.setOnClickListener(view -> {
            languagesId = 13;
            tvEnglish.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor));
            tvAll.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvHindi.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvGujarati.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvMarathi.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            popupWindow.dismiss();
            getCategoryWiseData(Integer.parseInt(templateId));
        });

        tvHindi.setOnClickListener(view -> {
            languagesId = 14;
            tvHindi.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor));
            tvAll.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvEnglish.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvGujarati.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvMarathi.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            popupWindow.dismiss();
            getCategoryWiseData(Integer.parseInt(templateId));
        });

        tvMarathi.setOnClickListener(view -> {
            languagesId = 16;
            tvMarathi.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor));
            tvAll.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvEnglish.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvGujarati.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvHindi.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            popupWindow.dismiss();
            getCategoryWiseData(Integer.parseInt(templateId));
        });

        tvGujarati.setOnClickListener(view -> {
            languagesId = 15;
            tvGujarati.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabSelectColor));
            tvAll.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvEnglish.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvMarathi.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            tvHindi.setTextColor(ContextCompat.getColor(ActivityMain.this, R.color.tabUnSelectColor));
            popupWindow.dismiss();
            getCategoryWiseData(Integer.parseInt(templateId));
        });
    }

    private void getDataFromApi() {
        binding.failedToGetLay.setVisibility(View.GONE);
        binding.postRV.setVisibility(View.GONE);
        binding.categoryRV.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        Call<CategoryName> call;
        if (tab.equalsIgnoreCase("Daily")) {
            call = apiEndpoints.getUserPersonalPostData(MyApplication.getAppDecryptKey(), BuildConfig.VERSION_CODE, 1, postType, languagesId);
        } else {
            call = apiEndpoints.getUserPersonalPostData(MyApplication.getAppDecryptKey(), BuildConfig.VERSION_CODE, 1, postType, languagesId);
        }
        call.enqueue(new Callback<CategoryName>() {
            @Override
            public void onResponse(@NonNull Call<CategoryName> call, @NonNull Response<CategoryName> response) {
                if (response.isSuccessful()) {

                    binding.searchLay.setEnabled(true);
                    binding.changeProfileLay.setEnabled(true);

                    categoryList = new ArrayList<>(response.body().getData().getCategory());
                    bannerPostList = new ArrayList<>(response.body().getData().getBanner());
                    bannerPostListWithAds = getBannerPostListWithAds(bannerPostList, 3);
                    totalCategoryPage = response.body().getData().getPagination().getTotalPages();
                    totalCategoryData = response.body().getData().getPagination().getTotalItems();
                    searchCategoryList = new ArrayList<>(response.body().getData().getCategory());

                    CategoryItem allCategory = new CategoryItem();
                    allCategory.setId(0);
                    allCategory.setName("All");
                    allCategory.setSelected(true);
                    categoryList.add(0, allCategory);

                    callMoreData = true;
                    setupRecyclerViewCategory();
                } else {
                    binding.shimmerLayout.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.postRV.setVisibility(View.GONE);
                    binding.categoryRV.setVisibility(View.GONE);
                    binding.failedToGetLay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryName> call, @NonNull Throwable t) {
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.postRV.setVisibility(View.GONE);
                binding.categoryRV.setVisibility(View.GONE);
                binding.failedToGetLay.setVisibility(View.VISIBLE);
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

    private void setPostData0() {
        if (MyPreference.get_IsProfileBusiness()) {
            adapterPost = new AdapterPost(ActivityMain.this, bannerPostList, bannerPostListWithAds, templateMainLayoutListBusinesses, totalCategoryData);
        } else {
            adapterPost = new AdapterPost(ActivityMain.this, bannerPostList, bannerPostListWithAds, templateMainLayoutListPersonal, totalCategoryData);
        }

        linearLayoutManager = new LinearLayoutManagerScroller(this);
        binding.postRV.setLayoutManager(linearLayoutManager);

        binding.postRV.setNestedScrollingEnabled(false);
        binding.postRV.setFitsSystemWindows(true);
        binding.postRV.setAdapter(adapterPost);

        binding.shimmerLayout.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
        binding.postRV.setVisibility(View.VISIBLE);
    }

    private void setPostData1() {
        setPostData0();
        if (rvScrollListener2 != null) {
            binding.postRV.removeOnScrollListener(rvScrollListener2);
        }
        rvScrollListener1 = new RvScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int i) {
                if (totalCategoryPage >= i && callMoreData) {
                    callMoreData = false;
                    getMoreData1(i);
                }
            }
        };
        binding.postRV.addOnScrollListener(rvScrollListener1);
    }

    private void setPostData2(int cateID) {
        setPostData0();
        if (rvScrollListener1 != null) {
            binding.postRV.removeOnScrollListener(rvScrollListener1);
        }
        rvScrollListener2 = new RvScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int i) {
                Log.e(TAG, "onLoadMore: i " + i);
                Log.e(TAG, "totalCategoryPage " + totalCategoryPage);
                Log.e(TAG, "onLoadMore: callMoreData " + callMoreData);
                if (totalCategoryPage >= i && callMoreData) {
                    callMoreData = false;
                    getMoreData2(cateID, i);
                }
            }
        };
        binding.postRV.addOnScrollListener(rvScrollListener2);
    }

    private void setupRecyclerViewCategory() {
        binding.categoryRV.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL));
        adapterCategoryList = new AdapterCategoryList(this, categoryList, new AdapterCategoryList.CategoryClick() {
            @Override
            public void onCategoryClick(int pos, int cateID) {
                Log.e(TAG, "onCategoryClick: " + cateID);
                if (pos == 0) {
                    templateId = "0";
                } else {
                    templateId = String.valueOf(cateID);
                }
                getCategoryWiseData(cateID);
            }
        });
        binding.categoryRV.setAdapter(adapterCategoryList);
        binding.categoryRV.setVisibility(View.VISIBLE);
        if (!templateId.equalsIgnoreCase("0")) {
            categoryList.get(0).setSelected(false);
            for (int i = 0; i < categoryList.size(); i++) {
                if (categoryList.get(i).getId() == Integer.parseInt(templateId)) {
                    categoryList.get(i).setSelected(true);
                    binding.categoryRV.scrollToPosition(i);
                    break;
                }
            }
        }
        if (!templateId.equalsIgnoreCase("0")) {
            getCategoryWiseData(Integer.parseInt(templateId));
        } else {
            setPostData1();
            binding.shimmerLayout.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            binding.postRV.setVisibility(View.VISIBLE);
        }

        MyAdsManager.loadRewardedAd(ActivityMain.this, true);
    }

    private void getCategoryWiseData(int cateID) {
        binding.failedToGetLay.setVisibility(View.GONE);
        binding.postRV.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        Call<CategoryWiseData> call = apiEndpoints.getUpdatedCategoryWiseData(cateID, MyApplication.getAppDecryptKey(), BuildConfig.VERSION_CODE, 1, postType, languagesId);
        call.enqueue(new Callback<CategoryWiseData>() {
            @Override
            public void onResponse(@NonNull Call<CategoryWiseData> call, @NonNull Response<CategoryWiseData> response) {
                if (response.isSuccessful()) {
                    if (response.body().getData() != null) {
                        bannerPostList.clear();
                        bannerPostList = new ArrayList<>();
                        bannerPostList = new ArrayList<>(response.body().getData().getPost());
                        bannerPostListWithAds.clear();
                        bannerPostListWithAds = new ArrayList<>();
                        bannerPostListWithAds = getBannerPostListWithAds(bannerPostList, 3);
                        totalCategoryPage = response.body().getData().getPagination().getTotalPages();
                        totalCategoryData = response.body().getData().getPagination().getTotalItems();
                        Log.e(TAG, "onResponse: totalCategoryPage " + totalCategoryPage);
                        Log.e(TAG, "onResponse: totalCategoryData " + totalCategoryData);
                        callMoreData = true;
                        setPostData2(cateID);
                    } else {
                        binding.shimmerLayout.setVisibility(View.GONE);
                        binding.progressBar.setVisibility(View.GONE);
                        binding.postRV.setVisibility(View.GONE);
                        binding.failedToGetLay.setVisibility(View.VISIBLE);
                    }

                } else {
                    binding.shimmerLayout.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.postRV.setVisibility(View.GONE);
                    binding.failedToGetLay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryWiseData> call, @NonNull Throwable t) {
                binding.shimmerLayout.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.postRV.setVisibility(View.GONE);
                binding.failedToGetLay.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getMoreData1(int page) {
        binding.progressBarTop.setVisibility(View.VISIBLE);

        Call<CategoryName> call;
        if (tab.equalsIgnoreCase("Daily")) {
            call = apiEndpoints.getUserPersonalPostData(MyApplication.getAppDecryptKey(), BuildConfig.VERSION_CODE, page, postType, languagesId);
        } else {
            call = apiEndpoints.getUserPersonalPostData(MyApplication.getAppDecryptKey(), BuildConfig.VERSION_CODE, page, postType, languagesId);
        }
        call.enqueue(new Callback<CategoryName>() {
            @Override
            public void onResponse(@NonNull Call<CategoryName> call, @NonNull Response<CategoryName> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getData() != null) {
                            callMoreData = true;
                            ArrayList<PostItem> templist = (ArrayList<PostItem>) response.body().getData().getBanner();
                            if (templist != null && !templist.isEmpty()) {
                                ArrayList<PostItem> templistAds = getBannerPostListWithAdsPagination(templist, 3, bannerPostList.size());
                                int position = bannerPostListWithAds.size();
                                bannerPostList.addAll(templist);
                                bannerPostListWithAds.addAll(templistAds);
                                binding.progressBarTop.setVisibility(View.GONE);

                                int itemCount = bannerPostListWithAds.size() - position;
                                if (itemCount > 0) {
                                    binding.postRV.post(new Runnable() {
                                        public void run() {
                                            adapterPost.notifyItemInserted(position);
                                        }
                                    });
                                } else {
                                    binding.progressBarTop.setVisibility(View.GONE);
                                }
                            } else {
                                binding.progressBarTop.setVisibility(View.GONE);
                            }
                        } else {
                            binding.progressBarTop.setVisibility(View.GONE);
                        }
                    } else {
                        binding.progressBarTop.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    binding.progressBarTop.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryName> call, @NonNull Throwable t) {
                binding.progressBarTop.setVisibility(View.GONE);
            }
        });
    }

    private void getMoreData2(int cateID, int page) {
        binding.progressBarTop.setVisibility(View.VISIBLE);

        Call<CategoryWiseData> call = apiEndpoints.getUpdatedCategoryWiseData(cateID, MyApplication.getAppDecryptKey(), BuildConfig.VERSION_CODE, page, postType, languagesId);
        call.enqueue(new Callback<CategoryWiseData>() {
            @Override
            public void onResponse(@NonNull Call<CategoryWiseData> call, @NonNull Response<CategoryWiseData> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getData() != null) {
                            callMoreData = true;
                            ArrayList<PostItem> templist = (ArrayList<PostItem>) response.body().getData().getPost();
                            if (templist != null && !templist.isEmpty()) {
                                ArrayList<PostItem> templistAds = getBannerPostListWithAdsPagination(templist, 3, bannerPostList.size());
                                int position = bannerPostListWithAds.size();
                                bannerPostList.addAll(templist);
                                bannerPostListWithAds.addAll(templistAds);
                                binding.progressBarTop.setVisibility(View.GONE);

                                int itemCount = bannerPostListWithAds.size() - position;
                                if (itemCount > 0) {
                                    binding.postRV.post(new Runnable() {
                                        public void run() {
                                            adapterPost.notifyItemInserted(position);
                                        }
                                    });
                                } else {
                                    binding.progressBarTop.setVisibility(View.GONE);
                                }
                            } else {
                                binding.progressBarTop.setVisibility(View.GONE);
                            }
                        } else {
                            binding.progressBarTop.setVisibility(View.GONE);
                        }
                    } else {
                        binding.progressBarTop.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryWiseData> call, @NonNull Throwable t) {
                binding.progressBarTop.setVisibility(View.GONE);
            }
        });
    }

    private void changeProfilePicture() {

        String dashBoardProfilePath;
        if (MyPreference.get_IsProfileBusiness()) {
            dashBoardProfilePath = MyPreference.get_B_ProfilePath();
        } else {
            dashBoardProfilePath = MyPreference.get_P_ProfilePath();
        }

        if (!TextUtils.isEmpty(dashBoardProfilePath)) {
            Glide.with(this).load(dashBoardProfilePath)
                    .placeholder(R.drawable.img_place_holder)
                    .into(binding.profileImg);
        } else {
            if (MyPreference.get_IsProfileBusiness()) {
                binding.profileImg.setImageResource(R.drawable.icon_dummy_business_01);
            } else {
                binding.profileImg.setImageResource(R.drawable.icon_dummy_personal_01);
            }
        }
    }

    private boolean deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFolder(file);
                }
            }
        }
        return folder.delete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        inAppUpdate.onActivityResult(requestCode, resultCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inAppUpdate.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        inAppUpdate.onResume();

        try {
            if (MyUtil.isAlternative) {
                MyUtil.isAlternative = false;
                MyUtil.isUpdate = false;
                changeProfile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (MyUtil.isUpdate) {
                MyUtil.isUpdate = false;
                changeProfilePicture();
                if (adapterPost != null && !bannerPostList.isEmpty()) {
                    adapterPost.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeProfile() {
        Dialog switchDialog = DialogUtil.getDialogObject(this, R.layout.dialog_switch_profile);
        switchDialog.setCancelable(false);

        AppCompatImageView dialogProfileImg = switchDialog.findViewById(R.id.dialogProfileImg);
        TextView dialogMessageTxt = switchDialog.findViewById(R.id.dialogMessageTxt);

        if (MyPreference.get_IsProfileBusiness()) {
            dialogProfileImg.setImageResource(R.drawable.icon_dialog_profile_business);
            dialogMessageTxt.setText(getText(R.string.switching_to_business));
        } else {
            dialogProfileImg.setImageResource(R.drawable.icon_dialog_profile_personal);
            dialogMessageTxt.setText(getText(R.string.switching_to_personal));
        }

        switchDialog.show();

        // Delayed dismissal of dialog after 2 seconds
        new Handler().postDelayed(() -> {
            if (switchDialog.isShowing()) {
                if (MyPreference.get_IsProfileBusiness()) {
                    binding.changeProfileImg.setImageResource(R.drawable.icon_main_screen_personal_profile);
                } else {
                    binding.changeProfileImg.setImageResource(R.drawable.icon_main_business_profile);
                }
                switchDialog.dismiss();
            }
        }, 2000);

        binding.postRV.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        setPostData0();
        changeProfilePicture();
    }

    public boolean checkNotificationPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int post_notification = ContextCompat.checkSelfPermission(ActivityMain.this, POST_NOTIFICATIONS);
            return post_notification == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void takeNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(ActivityMain.this, new String[]{POST_NOTIFICATIONS}, 1000);
        }
    }

    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMain.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialogInterface, i) -> {
            dialogInterface.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }

    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (MyPreference.get_IsPremium()) {
                    if (BackPressedTime + 2000 > System.currentTimeMillis()) {
                        try {
                            System.gc();
                            finishAffinity();
                            System.exit(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
                    }
                    BackPressedTime = System.currentTimeMillis();
                } else {
                    if (!isDestroyed() && !isFinishing()) {
                        startActivity(new Intent(ActivityMain.this, ActivityAppExit1.class));
                    }
                }
            }
        });
    }
}