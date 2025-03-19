package com.festival.dailypostermaker.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.Model.Language;
import com.festival.dailypostermaker.MyUtils.LocaleHelper;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.ActivityLanguageSelectionBinding;

import java.util.ArrayList;
import java.util.Locale;

public class ActivityLanguageSelection extends ActivityBase {

    private String selectedLanguageCode = "en";
    private ActivityLanguageSelectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguageSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupAd();
        setupRecyclerView();
        setupClickListeners();
        handleBackPress();
    }

    private void setupAd() {
        MyAdsManager.displayNativeSmallAd(this, findViewById(R.id.flNativeBanner));
    }

    private void setupRecyclerView() {
        String savedLanguage = MyPreference.get_LanguageCode();
        ArrayList<Language> languageOptions = getAvailableLanguages(savedLanguage);

        binding.languageRv.setLayoutManager(new LinearLayoutManager(this));
        AdapterLanguage adapter = new AdapterLanguage(this, languageOptions, language -> {
            selectedLanguageCode = language.langeCode;
        });
        binding.languageRv.setAdapter(adapter);
    }

    private ArrayList<Language> getAvailableLanguages(String savedLanguage) {
        ArrayList<Language> languages = new ArrayList<>();
        languages.add(new Language("English", "en", savedLanguage.equals("en")));
        languages.add(new Language("Hindi", "hi", savedLanguage.equals("hi")));
        languages.add(new Language("Marathi", "mr", savedLanguage.equals("mr")));
        languages.add(new Language("Gujarati", "gu", savedLanguage.equals("gu")));
        return languages;
    }

    private void setupClickListeners() {
        binding.backLay.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.languageSaveTxt.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                MyPreference.set_LanguageCode(selectedLanguageCode);
                LocaleHelper.setLocale(ActivityLanguageSelection.this, MyPreference.get_LanguageCode());
                navigateToNextScreen();
            }
        });
    }

    private void navigateToNextScreen() {
        if (MyPreference.isFirstTimeUser()) {
            MyAdsManager.displayInterstitialSecondWise(this, () -> {
                startActivity(new Intent(ActivityLanguageSelection.this, ActivityWelcome.class));
                finish();
            });
        } else {
            MyAdsManager.displayInterstitialSecondWise(this, () -> {
                Intent intent = new Intent(ActivityLanguageSelection.this, ActivityMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("TemplateId", "0");
                intent.putExtra("PostType", "0");
                startActivity(intent);
                finish();
            });
        }
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (MyPreference.isFirstTimeUser()) {
                    startActivity(new Intent(ActivityLanguageSelection.this, ActivityAppExit1.class));
                } else {
                    finish();
                }
            }
        });
    }

    public static class AdapterLanguage extends RecyclerView.Adapter<AdapterLanguage.ViewHolder> {

        private final Context context;
        private final ArrayList<Language> languageList;
        private final LanguageSelectionListener listener;
        private String currentLanguageCode;

        public AdapterLanguage(Context context, ArrayList<Language> languageList, LanguageSelectionListener listener) {
            this.context = context;
            this.languageList = languageList;
            this.listener = listener;
            this.currentLanguageCode = MyPreference.get_LanguageCode();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language_selection, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Language language = languageList.get(position);
            boolean isSelected = currentLanguageCode.equals(language.langeCode);

            holder.languageContainer.setBackgroundResource(isSelected ? R.drawable.bg_language_selected : R.drawable.bg_language_unselected);
            holder.languageIcon.setImageDrawable(ContextCompat.getDrawable(context,
                    isSelected ? R.drawable.language_selected_img : R.drawable.language_unselected_img));

            holder.languageNameTxt.setText(language.langName);
            Locale locale = new Locale(language.langeCode);
            holder.languageTitleTxt.setText(String.format("(%s)", locale.getDisplayLanguage(locale)));

            holder.languageContainer.setOnClickListener(view -> {
                currentLanguageCode = language.langeCode;
                notifyDataSetChanged();
                listener.onLanguageSelected(language);
            });
        }

        @Override
        public int getItemCount() {
            if (languageList == null)
                return 0;
            return languageList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            private final RelativeLayout languageContainer;
            private final AppCompatImageView languageIcon;
            private final TextView languageNameTxt;
            private final TextView languageTitleTxt;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                languageContainer = itemView.findViewById(R.id.languageContainer);
                languageIcon = itemView.findViewById(R.id.languageIcon);
                languageNameTxt = itemView.findViewById(R.id.languageNameTxt);
                languageTitleTxt = itemView.findViewById(R.id.languageTitleTxt);
            }
        }
    }

    public interface LanguageSelectionListener {
        void onLanguageSelected(Language language);
    }
}