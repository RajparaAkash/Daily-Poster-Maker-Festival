package com.festival.dailypostermaker.Activity;

import static com.festival.dailypostermaker.MyUtils.MyUtil.getSubBgData;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Adapter.AdapterBgCategoryList;
import com.festival.dailypostermaker.Adapter.AdapterBgDialogDownloaded;
import com.festival.dailypostermaker.Adapter.AdapterBgDialogOnline;
import com.festival.dailypostermaker.Adapter.AdapterBgDownloaded;
import com.festival.dailypostermaker.Api.ApiEndpoints;
import com.festival.dailypostermaker.Api.RetrofitClient;
import com.festival.dailypostermaker.BuildConfig;
import com.festival.dailypostermaker.Model.Bg.Bg;
import com.festival.dailypostermaker.Model.Bg.BgItem;
import com.festival.dailypostermaker.Model.Bg.BgSub;
import com.festival.dailypostermaker.MyApplication;
import com.festival.dailypostermaker.MyUtils.DownloadListener;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.Service.DownloadCompleteReceiver;
import com.festival.dailypostermaker.databinding.ActivityCreatePersonalPostBinding;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ActivityCreatePersonalPost extends ActivityBase {

    AdapterBgDialogOnline adapterBgDialogOnline;
    AdapterBgDialogDownloaded adapterBgDialogDownloaded;
    private AdapterBgCategoryList adapterBgCategoryList;

    ArrayList<BgItem> assetsList;
    ArrayList<BgSub> bgSubList = new ArrayList<>();
    ArrayList<BgSub> filteredBgSubList = new ArrayList<>();

    private DownloadCompleteReceiver downloadCompleteReceiver;
    private ActivityResultLauncher<PickVisualMediaRequest> pickPhoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    startCropActivity(uri);
                }
            });

    private ActivityCreatePersonalPostBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePersonalPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        pickPhoto = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                startCropActivity(uri);
            }
        });

        binding.categoryBgRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.backgroundRv.setLayoutManager(new GridLayoutManager(this, 2));

        setupAd();
        setupCategoryBgAdapter();
        setupClickListeners();
        handleBackPress();

        // Create DownloadCompleteReceiver with the listener
        downloadCompleteReceiver = new DownloadCompleteReceiver(new DownloadListener() {
            @Override
            public void onDownloadComplete() {
                // Perform additional actions after successful download
                if (adapterBgDialogOnline != null) {
                    adapterBgDialogOnline.dismissDialog();
                }
            }

            @Override
            public void onDownloadFailed() {
                if (adapterBgDialogOnline != null) {
                    adapterBgDialogOnline.dismissDialog2();
                }
            }
        });

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(downloadCompleteReceiver, filter, RECEIVER_EXPORTED);
        } else {
            registerReceiver(downloadCompleteReceiver, filter);
        }
    }

    private void setupAd() {
        MyAdsManager.displayAdaptiveBannerAd(this, findViewById(R.id.llBanner));
    }

    private void setupCategoryBgAdapter() {
        bgSubList.clear();
        filteredBgSubList.clear();

        bgSubList = getSubBgData(ActivityCreatePersonalPost.this);
        filteredBgSubList.addAll(bgSubList);

        if (!bgSubList.isEmpty()) {
            adapterBgCategoryList = new AdapterBgCategoryList(this, filteredBgSubList, new AdapterBgCategoryList.Click() {
                @Override
                public void onCategoryClick(int pos) {
                    setBackgroundAdapter(pos);
                    binding.backgroundRv.setVisibility(View.VISIBLE);
                }
            });
            binding.categoryBgRV.setAdapter(adapterBgCategoryList);
            setBackgroundAdapter(0);

            binding.addBackgroundLay.setVisibility(View.GONE);
            binding.categoryBgRV.setVisibility(View.VISIBLE);
            binding.backgroundRv.setVisibility(View.VISIBLE);
        } else {
            binding.appBarLayout.setExpanded(true, true);
            binding.categoryBgRV.setVisibility(View.GONE);
            binding.backgroundRv.setVisibility(View.GONE);
            binding.addBackgroundLay.setVisibility(View.VISIBLE);
        }
    }

    private void refreshCategoryBgAdapter() {
        if (!bgSubList.isEmpty()) {
            adapterBgCategoryList = new AdapterBgCategoryList(this, filteredBgSubList, new AdapterBgCategoryList.Click() {
                @Override
                public void onCategoryClick(int pos) {
                    setBackgroundAdapter(pos);
                    binding.backgroundRv.setVisibility(View.VISIBLE);
                }
            });
            binding.categoryBgRV.setAdapter(adapterBgCategoryList);
            setBackgroundAdapter(0);

            binding.addBackgroundLay.setVisibility(View.GONE);
            binding.categoryBgRV.setVisibility(View.VISIBLE);
            binding.backgroundRv.setVisibility(View.VISIBLE);
        } else {
            binding.categoryBgRV.setVisibility(View.GONE);
            binding.backgroundRv.setVisibility(View.GONE);
            binding.addBackgroundLay.setVisibility(View.VISIBLE);
        }
    }

    private void setBackgroundAdapter(int categoryPos) {
        AdapterBgDownloaded adapterBgDownloaded = new AdapterBgDownloaded(this, filteredBgSubList.get(categoryPos).getMediaPaths());
        binding.backgroundRv.setAdapter(adapterBgDownloaded);
    }

    private void setupClickListeners() {
        binding.backLay.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.uploadImgLy.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                pickPhoto.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        binding.addBgLay1.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                dialogBackground();
            }
        });

        binding.addBgLay2.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                dialogBackground();
            }
        });

        binding.searchBgEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchText = binding.searchBgEt.getText().toString();
                    filterCategoryList(searchText);
                    MyUtil.hideKeyBoard(ActivityCreatePersonalPost.this, binding.searchBgEt);
                    return true;
                }
                return false;
            }
        });

        binding.searchBgEt.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // Scroll the AppBarLayout up when EditText gains focus
                binding.appBarLayout.setExpanded(false, true);
            }
        });
    }

    private void filterCategoryList(String searchText) {
        if (bgSubList.isEmpty()) {
            Toast.makeText(ActivityCreatePersonalPost.this, "Please Download Background First", Toast.LENGTH_SHORT).show();
            return;
        }

        filteredBgSubList.clear();
        for (BgSub bgSub : bgSubList) {
            if (bgSub.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredBgSubList.add(bgSub);
            }
        }

        if (filteredBgSubList.isEmpty()) {
            binding.backgroundRv.setVisibility(View.GONE);
            binding.tvNoDataFound.setVisibility(View.VISIBLE);
        } else {
            binding.tvNoDataFound.setVisibility(View.GONE);
            binding.backgroundRv.setVisibility(View.VISIBLE);
        }

        if (filteredBgSubList.size() == bgSubList.size()) {
            bgSubList.clear();
            filteredBgSubList.clear();
            setupCategoryBgAdapter();
            return;
        }

        if (!filteredBgSubList.isEmpty()) {
            for (BgSub c : filteredBgSubList) {
                c.setSelected(false);
            }
            filteredBgSubList.get(0).setSelected(true);
            adapterBgCategoryList.notifyDataSetChanged();
            setBackgroundAdapter(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK && data != null) {
            Uri croppedImageUri = UCrop.getOutput(data);
            handleCrop(MyUtil.getPathFromURI(this, croppedImageUri));
        }
    }

    private void startCropActivity(Uri imageUri) {
        UCrop.Options options = new UCrop.Options();
        UCrop.of(imageUri, Uri.fromFile(new File(getCacheDir(), "cropImage_" + System.currentTimeMillis() + ".png")))
                .withAspectRatio(1, 1)
                .withOptions(options)
                .start(this);
    }

    private void handleCrop(String imageFilePath) {
        MyAdsManager.displayInterstitialSecondWise(ActivityCreatePersonalPost.this, () -> {
            Intent intent = new Intent(ActivityCreatePersonalPost.this, ActivityCreateEditPost.class);
            intent.putExtra("imagePath", imageFilePath);
            startActivity(intent);
        });
    }

    public void dialogBackground() {
        BottomSheetDialog bgDialog = new BottomSheetDialog(this, R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_backgroud, null);
        bgDialog.setContentView(bottomSheetView);
        bgDialog.show();

        AppCompatImageView dialogCloseImg = bgDialog.findViewById(R.id.dialogCloseImg);
        TextView allBgTxt = bgDialog.findViewById(R.id.allBgTxt);
        TextView myBgTxt = bgDialog.findViewById(R.id.myBgTxt);

        RelativeLayout allLay = bgDialog.findViewById(R.id.allLay);
        RecyclerView onlineBgRv = bgDialog.findViewById(R.id.onlineBgRv);
        ProgressBar progressBar1 = bgDialog.findViewById(R.id.progressBar1);
        TextView errorTxt1 = bgDialog.findViewById(R.id.errorTxt1);

        RelativeLayout myLay = bgDialog.findViewById(R.id.myLay);
        RecyclerView downloadedBgRv = bgDialog.findViewById(R.id.downloadedBgRv);
        ProgressBar progressBar2 = bgDialog.findViewById(R.id.progressBar2);
        TextView errorTxt2 = bgDialog.findViewById(R.id.errorTxt2);

        dialogCloseImg.setOnClickListener(view -> {
            bgDialog.dismiss();
        });

        allBgTxt.setOnClickListener(view -> {
            allBgTxt.setBackgroundResource(R.drawable.bg_background_selected);
            myBgTxt.setBackgroundResource(R.drawable.bg_background_unselected);

            allBgTxt.setTextColor(getColor(R.color.white));
            myBgTxt.setTextColor(getColor(R.color.textColor));

            myLay.setVisibility(View.GONE);
            allLay.setVisibility(View.VISIBLE);
        });

        myBgTxt.setOnClickListener(view -> {
            allBgTxt.setBackgroundResource(R.drawable.bg_background_unselected);
            myBgTxt.setBackgroundResource(R.drawable.bg_background_selected);

            allBgTxt.setTextColor(getColor(R.color.textColor));
            myBgTxt.setTextColor(getColor(R.color.white));

            allLay.setVisibility(View.GONE);
            myLay.setVisibility(View.VISIBLE);
        });


        errorTxt1.setVisibility(View.GONE);
        onlineBgRv.setVisibility(View.GONE);
        progressBar1.setVisibility(View.VISIBLE);

        ApiEndpoints apiEndpoints = RetrofitClient.getInstance().create(ApiEndpoints.class);

        Call<Bg> call = apiEndpoints.getAssetsData(BuildConfig.VERSION_CODE, MyApplication.getAppDecryptKey());
        call.enqueue(new Callback<Bg>() {
            @Override
            public void onResponse(Call<Bg> call, retrofit2.Response<Bg> response) {
                if (response.isSuccessful()) {

                    for (int i = 0; i < response.body().getData().size(); i++) {
                        assetsList = response.body().getData().get(0).getAssets();
                    }

                    if (assetsList.size() != 0) {
                        onlineBgRv.setLayoutManager(new LinearLayoutManager(ActivityCreatePersonalPost.this, LinearLayoutManager.VERTICAL, false));
                        adapterBgDialogOnline = new AdapterBgDialogOnline(bgDialog, ActivityCreatePersonalPost.this, assetsList, new AdapterBgDialogOnline.Click() {
                            @Override
                            public void onDownloadClick() {
                                filteredBgSubList.clear();
                                bgSubList = getSubBgData(ActivityCreatePersonalPost.this);
                                filteredBgSubList.addAll(bgSubList);

                                if (bgSubList.size() != 0) {
                                    downloadedBgRv.setLayoutManager(new LinearLayoutManager(ActivityCreatePersonalPost.this, LinearLayoutManager.VERTICAL, false));
                                    adapterBgDialogDownloaded = new AdapterBgDialogDownloaded(ActivityCreatePersonalPost.this, bgDialog, bgSubList, new AdapterBgDialogDownloaded.Click() {
                                        @Override
                                        public void onDeleteClick(int size) {
                                            if (size == 0) {
                                                downloadedBgRv.setVisibility(View.GONE);
                                                errorTxt2.setVisibility(View.VISIBLE);
                                            }

                                            if (adapterBgDialogOnline != null) {
                                                adapterBgDialogOnline.notifyDataSetChanged();
                                            }
                                        }
                                    });

                                    downloadedBgRv.setAdapter(adapterBgDialogDownloaded);

                                    progressBar2.setVisibility(View.GONE);
                                    errorTxt2.setVisibility(View.GONE);
                                    downloadedBgRv.setVisibility(View.VISIBLE);

                                } else {
                                    progressBar2.setVisibility(View.GONE);
                                    downloadedBgRv.setVisibility(View.GONE);
                                    errorTxt2.setVisibility(View.VISIBLE);
                                }

                                refreshCategoryBgAdapter();
                            }
                        });
                        onlineBgRv.setAdapter(adapterBgDialogOnline);
                        progressBar1.setVisibility(View.GONE);
                        onlineBgRv.setVisibility(View.VISIBLE);

                    } else {
                        progressBar1.setVisibility(View.GONE);
                        onlineBgRv.setVisibility(View.GONE);
                        errorTxt1.setVisibility(View.VISIBLE);
                    }

                } else {
                    progressBar1.setVisibility(View.GONE);
                    onlineBgRv.setVisibility(View.GONE);
                    errorTxt1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Bg> call, Throwable t) {
                progressBar1.setVisibility(View.GONE);
                onlineBgRv.setVisibility(View.GONE);
                errorTxt1.setVisibility(View.VISIBLE);
            }
        });


        downloadedBgRv.setVisibility(View.GONE);
        progressBar2.setVisibility(View.VISIBLE);

        ArrayList<BgSub> downloadedBgList = getSubBgData(this);
        if (downloadedBgList.size() != 0) {
            downloadedBgRv.setLayoutManager(new LinearLayoutManager(ActivityCreatePersonalPost.this, LinearLayoutManager.VERTICAL, false));
            adapterBgDialogDownloaded = new AdapterBgDialogDownloaded(ActivityCreatePersonalPost.this, bgDialog, downloadedBgList, new AdapterBgDialogDownloaded.Click() {
                @Override
                public void onDeleteClick(int size) {
                    if (size == 0) {
                        downloadedBgRv.setVisibility(View.GONE);
                        errorTxt2.setVisibility(View.VISIBLE);
                    }

                    if (adapterBgDialogOnline != null) {
                        adapterBgDialogOnline.notifyDataSetChanged();
                    }

                    setupCategoryBgAdapter();
                }
            });
            downloadedBgRv.setAdapter(adapterBgDialogDownloaded);

            progressBar2.setVisibility(View.GONE);
            errorTxt2.setVisibility(View.GONE);
            downloadedBgRv.setVisibility(View.VISIBLE);

        } else {
            progressBar2.setVisibility(View.GONE);
            downloadedBgRv.setVisibility(View.GONE);
            errorTxt2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver
        unregisterReceiver(downloadCompleteReceiver);
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