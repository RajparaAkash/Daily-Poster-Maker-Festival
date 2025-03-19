package com.festival.dailypostermaker.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Adapter.AdapterEditViewPager;
import com.festival.dailypostermaker.Fragment.FragmentEditAlign;
import com.festival.dailypostermaker.Fragment.FragmentEditBoxColor;
import com.festival.dailypostermaker.Fragment.FragmentEditColor;
import com.festival.dailypostermaker.Fragment.FragmentEditFont;
import com.festival.dailypostermaker.Fragment.FragmentEditShadow;
import com.festival.dailypostermaker.Interface.ColorFragmentListener;
import com.festival.dailypostermaker.Interface.FontFragmentListener;
import com.festival.dailypostermaker.Interface.FormatTextFragmentListener;
import com.festival.dailypostermaker.Interface.HightLightFragmentListener;
import com.festival.dailypostermaker.Interface.ShadowFragmentListener;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.PhotoEditor.OnPhotoEditorListener;
import com.festival.dailypostermaker.PhotoEditor.PhotoEditor;
import com.festival.dailypostermaker.PhotoEditor.RoundFrameLayout;
import com.festival.dailypostermaker.PhotoEditor.RoundViewDelegate;
import com.festival.dailypostermaker.PhotoEditor.SaveSettings;
import com.festival.dailypostermaker.PhotoEditor.StrokeTextView;
import com.festival.dailypostermaker.PhotoEditor.ViewType;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivityCreateEditPostBinding;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

public class ActivityCreateEditPost extends ActivityBase implements ColorFragmentListener, FontFragmentListener, FormatTextFragmentListener, HightLightFragmentListener, ShadowFragmentListener, OnPhotoEditorListener {

    private RoundFrameLayout border;
    private int colorBackground;
    public PhotoEditor mPhotoEditor;
    public int numberAddedView;
    private int styleText;
    private StrokeTextView textViewMain;
    private int colorTextShadow = Color.parseColor("#000000");
    public int currentTabTool = 0;
    private String opticalBackground = "66";
    private int opticalText = 255;
    private int rRadius = 0;
    private int rY = 0;
    private Dialog pleaseWaitDialog;

    private ActivityCreateEditPostBinding binding;

    @Override
    public void onStartViewChangeListener(ViewType viewType) {
    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityCreateEditPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyUtil.edit_font_pos = -1;
        MyUtil.edit_box_color_pos = -1;
        MyUtil.edit_color_pos = -1;
        MyUtil.edit_shadow_color_pos = -1;

        setupAd();
        setupClickListeners();
        handleBackPress();

        PhotoEditor build = new PhotoEditor.Builder(this, binding.imgPhotoEditor).setPinchTextScalable(true)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(), "font/font5.TTF")).build();
        this.mPhotoEditor = build;
        build.setOnPhotoEditorListener(this);

        getData();
        setViewPagerWithTab();
    }

    private void setupAd() {
        MyAdsManager.displayAdaptiveBannerAd(this, findViewById(R.id.llBanner));
    }

    private void getData() {
        MyUtil.changedText = "Double tap to edit !";
        Intent intent = getIntent();
        if (intent != null) {
            String imagePath = intent.getStringExtra("imagePath");
            Glide.with(this).load(imagePath).into(binding.imgPhotoEditor.getSource());
            this.mPhotoEditor.addText(MyUtil.changedText, ContextCompat.getColor(this, R.color.white));
        }
    }

    private void setupClickListeners() {
        binding.backLay.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.nextTxt.setOnClickListener(view -> {
            saveTextArt();
        });

        binding.imgPhotoEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotoEditor.clearHelperBox();
            }
        });
    }

    public void setViewPagerWithTab() {
        setupViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.tabLayout.getTabAt(0).setCustomView(getHeaderView());
        binding.tabLayout.getTabAt(1).setCustomView(getHeaderView());
        binding.tabLayout.getTabAt(2).setCustomView(getHeaderView());
        binding.tabLayout.getTabAt(3).setCustomView(getHeaderView());
        binding.tabLayout.getTabAt(4).setCustomView(getHeaderView());

        for (int i = 0; i < binding.tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = binding.tabLayout.getTabAt(i);
            AppCompatImageView tabImg = tab.getCustomView().findViewById(R.id.tabImg);
            TextView tabTxt = tab.getCustomView().findViewById(R.id.tabTxt);

            if (i == 0) {
                tabImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ActivityCreateEditPost.this,R.color.tabSelectColor)));
                tabTxt.setTextColor(ContextCompat.getColor(ActivityCreateEditPost.this,R.color.tabSelectColor));

                tabImg.setImageResource(R.drawable.icon_tab_edit_post_01);
                tabTxt.setText(getResources().getString(R.string.font));

            } else {
                tabImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ActivityCreateEditPost.this,R.color.tabUnSelectColor)));
                tabTxt.setTextColor(ContextCompat.getColor(ActivityCreateEditPost.this,R.color.tabUnSelectColor));

                if (i == 1) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_02);
                    tabTxt.setText(getResources().getString(R.string.box_color));
                } else if (i == 2) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_03);
                    tabTxt.setText(getResources().getString(R.string.color));
                } else if (i == 3) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_04);
                    tabTxt.setText(getResources().getString(R.string.align));
                } else if (i == 4) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_05);
                    tabTxt.setText(getResources().getString(R.string.shadow));
                }
            }
        }

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                AppCompatImageView tabImg = tab.getCustomView().findViewById(R.id.tabImg);
                TextView tabTxt = tab.getCustomView().findViewById(R.id.tabTxt);

                tabImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ActivityCreateEditPost.this,R.color.tabSelectColor)));
                tabTxt.setTextColor(ContextCompat.getColor(ActivityCreateEditPost.this,R.color.tabSelectColor));

                if (tab.getText().equals("1")) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_01);
                    tabTxt.setText(getResources().getString(R.string.font));
                } else if (tab.getText().equals("2")) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_02);
                    tabTxt.setText(getResources().getString(R.string.box_color));
                } else if (tab.getText().equals("3")) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_03);
                    tabTxt.setText(getResources().getString(R.string.color));
                } else if (tab.getText().equals("4")) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_04);
                    tabTxt.setText(getResources().getString(R.string.align));
                } else if (tab.getText().equals("5")) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_05);
                    tabTxt.setText(getResources().getString(R.string.shadow));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                AppCompatImageView tabImg = tab.getCustomView().findViewById(R.id.tabImg);
                TextView tabTxt = tab.getCustomView().findViewById(R.id.tabTxt);

                tabImg.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(ActivityCreateEditPost.this,R.color.tabUnSelectColor)));
                tabTxt.setTextColor(ContextCompat.getColor(ActivityCreateEditPost.this,R.color.tabUnSelectColor));

                if (tab.getText().equals("1")) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_01);
                    tabTxt.setText(getResources().getString(R.string.font));
                } else if (tab.getText().equals("2")) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_02);
                    tabTxt.setText(getResources().getString(R.string.box_color));
                } else if (tab.getText().equals("3")) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_03);
                    tabTxt.setText(getResources().getString(R.string.color));
                } else if (tab.getText().equals("4")) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_04);
                    tabTxt.setText(getResources().getString(R.string.align));
                } else if (tab.getText().equals("5")) {
                    tabImg.setImageResource(R.drawable.icon_tab_edit_post_05);
                    tabTxt.setText(getResources().getString(R.string.shadow));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        int leftMargin = -10; // Margin for the first tab item from the left
        int rightMargin = -10; // Margin for the second tab item from the right

        ViewGroup slidingTabStrip = (ViewGroup) binding.tabLayout.getChildAt(0);

        View firstTab = slidingTabStrip.getChildAt(0);
        ViewGroup.MarginLayoutParams firstTabParams = (ViewGroup.MarginLayoutParams) firstTab.getLayoutParams();
        firstTabParams.leftMargin = leftMargin;
        firstTabParams.rightMargin = rightMargin;

        View secondTab = slidingTabStrip.getChildAt(1);
        ViewGroup.MarginLayoutParams secondTabParams = (ViewGroup.MarginLayoutParams) secondTab.getLayoutParams();
        secondTabParams.leftMargin = leftMargin;
        secondTabParams.rightMargin = rightMargin;
    }

    private View getHeaderView() {
        return ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_custom_tab_edit_post, null, false);
    }

    private void setupViewPager(ViewPager viewPager) {
        AdapterEditViewPager adapter = new AdapterEditViewPager(getSupportFragmentManager());

        FragmentEditFont fragmentEditFont = new FragmentEditFont();
        FragmentEditBoxColor fragmentEditBoxColor = new FragmentEditBoxColor();
        FragmentEditColor fragmentEditColor = new FragmentEditColor();
        FragmentEditAlign fragmentEditAlign = new FragmentEditAlign();
        FragmentEditShadow fragmentEditShadow = new FragmentEditShadow();

        adapter.addFragment(fragmentEditFont, "1");
        adapter.addFragment(fragmentEditBoxColor, "2");
        adapter.addFragment(fragmentEditColor, "3");
        adapter.addFragment(fragmentEditAlign, "4");
        adapter.addFragment(fragmentEditShadow, "5");

        fragmentEditFont.setListener(this);
        fragmentEditBoxColor.setListener(this);
        fragmentEditColor.setListener(this);
        fragmentEditAlign.setListener(this);
        fragmentEditShadow.setListener(this);

        viewPager.setAdapter(adapter);
    }

    private void saveTextArt() {
        showPleaseWaitDialog();

        File outputMediaFile = MyUtil.getOutputMediaFile(this);
        Log.d("TAG", "outputMediaFile: " + outputMediaFile.getPath());
        if (outputMediaFile != null) {
            try {
                Boolean valueOf = Boolean.valueOf(outputMediaFile.createNewFile());
                Log.e("TAG", valueOf + "");
            } catch (IOException e) {
                e.printStackTrace();
                dismissPleaseWaitDialog();
                return;
            }
        }

        SaveSettings build = new SaveSettings.Builder().setClearViewsEnabled(false).setTransparencyEnabled(true).build();

        this.mPhotoEditor.saveAsFile(Executors.newSingleThreadExecutor(), new Handler(Looper.getMainLooper()), outputMediaFile.getAbsolutePath(), build, new PhotoEditor.OnSaveListener() {
            @Override
            public void onFailure(Exception exc) {
                dismissPleaseWaitDialog();
            }

            @Override
            public void onSuccess(String str) {
                dismissPleaseWaitDialog();

                MediaScannerConnection.scanFile(ActivityCreateEditPost.this,
                        new String[]{new File(str).getAbsolutePath()}, null, null);
                MyAdsManager.displayInterstitialSecondWise(ActivityCreateEditPost.this, () -> {
                    Intent intent = new Intent(ActivityCreateEditPost.this, ActivityCreateSavedPost.class);
                    intent.putExtra("withOutFrame", str);
                    startActivity(intent);
                });
            }
        });
    }

    @Override
    public void onEditTextChangeListener(View view, String str, int i) {
        MyAdsManager.displayInterstitialSecondWise(ActivityCreateEditPost.this, () -> {
            startActivity(new Intent(ActivityCreateEditPost.this, ActivityCreateAddText.class));
            overridePendingTransition(0, 0);
        });
    }

    @Override
    public void onAdded(StrokeTextView strokeTextView, RoundFrameLayout roundFrameLayout) {
        this.textViewMain = strokeTextView;
        this.border = roundFrameLayout;
    }

    @Override
    public void onClickGetEditTextChangeListener(StrokeTextView strokeTextView, RoundFrameLayout roundFrameLayout) {
        this.textViewMain = strokeTextView;
        this.border = roundFrameLayout;
    }

    @Override
    public void onAddViewListener(ViewType viewType, int i) {
        this.numberAddedView = i;
    }

    @Override
    public void onRemoveViewListener(int i) {
        this.numberAddedView = i;
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int i) {
        if (this.currentTabTool == 0) {
            this.currentTabTool = 1;
        }
    }

    @Override
    public void onTextAlign(int i) {
        if (i == 1) {
            this.textViewMain.setGravity(3);
        } else if (i == 2) {
            this.textViewMain.setGravity(17);
        } else if (i == 3) {
            this.textViewMain.setGravity(5);
        }
    }

    @Override
    public void onTextStyle(int i) {
        switch (i) {
            case 1:
                StrokeTextView strokeTextView = this.textViewMain;
                strokeTextView.setTypeface(strokeTextView.getTypeface(), Typeface.BOLD_ITALIC);
                this.styleText = i;
                return;
            case 2:
                StrokeTextView strokeTextView2 = this.textViewMain;
                strokeTextView2.setTypeface(strokeTextView2.getTypeface(), Typeface.BOLD);
                this.styleText = i;
                return;
            case 3:
                StrokeTextView strokeTextView3 = this.textViewMain;
                strokeTextView3.setTypeface(strokeTextView3.getTypeface(), Typeface.ITALIC);
                this.styleText = i;
                return;
            case 4:
                StrokeTextView strokeTextView4 = this.textViewMain;
                strokeTextView4.setTypeface(Typeface.create(strokeTextView4.getTypeface(), Typeface.NORMAL));
                this.styleText = i;
                return;
            case 5:
                this.textViewMain.setAllCaps(true);
                return;
            case 6:
                this.textViewMain.setAllCaps(false);
                return;
            default:
                return;
        }
    }

    @Override
    public void onTextSize(int i) {
        this.textViewMain.setTextSize(i);
    }

    @Override
    public void onTextPadding(int i) {
        this.border.setPadding(i, i, i, i);
    }

    @Override
    public void onFontSelected(String str) {
        AssetManager assets = getAssets();
        Typeface createFromAsset = Typeface.createFromAsset(assets, "font/" + str);
        int i = this.styleText;
        if (i == 1) {
            this.textViewMain.setTypeface(createFromAsset, Typeface.BOLD_ITALIC);
        } else if (i == 2) {
            this.textViewMain.setTypeface(createFromAsset, Typeface.BOLD);
        } else if (i == 3) {
            this.textViewMain.setTypeface(createFromAsset, Typeface.ITALIC);
        } else {
            this.textViewMain.setTypeface(createFromAsset, Typeface.NORMAL);
        }
    }

    @Override
    public void onHightLightColorSelected(int i) {
        this.colorBackground = i;
        String format = String.format("%06X", Integer.valueOf(i & ViewCompat.MEASURED_SIZE_MASK));
        RoundViewDelegate delegate = this.border.getDelegate();

        String color = "#" + this.opticalBackground + format;
        if (color.contains("F3F2F2")) {
            color = "#00F3F2F2";
        }
        delegate.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public void onHightLightColorOpacityChangeListerner(String str) {
        String format = String.format("%06X", Integer.valueOf(this.colorBackground & ViewCompat.MEASURED_SIZE_MASK));
        this.opticalBackground = str;
        RoundViewDelegate delegate = this.border.getDelegate();

        String color = "#" + this.opticalBackground + format;
        if (color.contains("F3F2F2")) {
            color = "#00F3F2F2";
        }
        delegate.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public void onHighLightRadius(int i) {
        this.border.getDelegate().setCornerRadius(i);
    }

    @Override
    public void onColorSelected(int i) {
        this.colorTextShadow = i;
        this.textViewMain.getPaint().setShader(null);
        this.textViewMain.setTextColor(i);
        StrokeTextView strokeTextView = this.textViewMain;
        strokeTextView.setTextColor(strokeTextView.getTextColors().withAlpha(this.opticalText));
    }

    @Override
    public void onColorOpacityChangeListerner(int i) {
        this.opticalText = i;
        StrokeTextView strokeTextView = this.textViewMain;
        strokeTextView.setTextColor(strokeTextView.getTextColors().withAlpha(i));
    }

    @Override
    public void onDyShadowChangeListener(int i) {
        this.rY = i;
        this.textViewMain.setStrokeWidth(0);
        float f = i;
        this.textViewMain.setShadowLayer(this.rRadius, f, f, this.colorTextShadow);
    }

    @Override
    public void onRadiusChangeListener(int i) {
        this.rRadius = i;
        this.textViewMain.setStrokeWidth(0);
        float f = this.rY;
        this.textViewMain.setShadowLayer(this.rRadius, f, f, this.colorTextShadow);
    }

    @Override
    public void onShadowColorSelected(int i) {
        this.colorTextShadow = i;
        this.textViewMain.setStrokeWidth(0);
        float f = this.rY;
        this.textViewMain.setShadowLayer(this.rRadius, f, f, this.colorTextShadow);
    }

    @Override
    protected void onResume() {
        super.onResume();
        textViewMain.setText(MyUtil.changedText);
    }

    public void showPleaseWaitDialog() {
        try {
            pleaseWaitDialog = new Dialog(this);
            pleaseWaitDialog.setContentView(R.layout.dialog_please_wait);
            if (pleaseWaitDialog.getWindow() != null) {
                pleaseWaitDialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                pleaseWaitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            pleaseWaitDialog.setCanceledOnTouchOutside(false);
            pleaseWaitDialog.setCancelable(false);

            if (!isFinishing() && !isDestroyed() && pleaseWaitDialog != null) {
                pleaseWaitDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissPleaseWaitDialog() {
        if (!isFinishing() && !isDestroyed() && pleaseWaitDialog != null && pleaseWaitDialog.isShowing()) {
            pleaseWaitDialog.dismiss();
        }
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(ActivityCreateEditPost.this)
                        .setMessage(R.string.are_you_sure_you_want_to_discard_all_changes)
                        .setPositiveButton(R.string.discard, (dialog, which) -> {
                            finish();
                        })
                        .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .create()
                        .show();
            }
        });
    }
}
