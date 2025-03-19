package com.festival.dailypostermaker.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.festival.dailypostermaker.Adapter.AdapterEditFont;
import com.festival.dailypostermaker.Interface.FontFragmentListener;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.databinding.FragmentEditFontBinding;

import java.util.Arrays;
import java.util.List;

public class FragmentEditFont extends Fragment {

    private AdapterEditFont adapterEditFont;
    private FontFragmentListener fontFragmentListener;
    private List<String> fontList;

    private FragmentEditFontBinding binding;

    public FragmentEditFont() {
    }

    public void setListener(FontFragmentListener fontFragmentListener) {
        this.fontFragmentListener = fontFragmentListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        binding = FragmentEditFontBinding.inflate(layoutInflater, viewGroup, false);

        setupFontRecyclerView();
        setupFontSizeSeekBar();
        setupFontStyleListeners();

        return binding.getRoot();
    }

    private void setupFontRecyclerView() {
        fontList = loadFontList();
        binding.epFontRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        adapterEditFont = new AdapterEditFont(getActivity(), fontList, pos -> {
            if (fontFragmentListener != null) {
                MyUtil.edit_font_pos = pos;
                adapterEditFont.notifyDataSetChanged();
                fontFragmentListener.onFontSelected(fontList.get(pos));
            }
        });

        binding.epFontRv.setAdapter(adapterEditFont);
    }

    private void setupFontSizeSeekBar() {
        binding.fontSizeSeekBar.setMax(60);
        binding.fontSizeSeekBar.setProgress(15);

        binding.fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No action needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No action needed
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fontFragmentListener != null) {
                    fontFragmentListener.onTextSize(progress);
                }
            }
        });
    }

    private void setupFontStyleListeners() {
        binding.fontBoldImg.setOnCheckedChangeListener((buttonView, isChecked) -> updateTextStyle());
        binding.fontItalicImg.setOnCheckedChangeListener((buttonView, isChecked) -> updateTextStyle());
    }

    private void updateTextStyle() {
        if (fontFragmentListener == null) return;

        boolean isBold = binding.fontBoldImg.isChecked();
        boolean isItalic = binding.fontItalicImg.isChecked();

        if (isBold && isItalic) {
            fontFragmentListener.onTextStyle(1); // Bold and Italic
        } else if (isBold) {
            fontFragmentListener.onTextStyle(2); // Bold only
        } else if (isItalic) {
            fontFragmentListener.onTextStyle(3); // Italic only
        } else {
            fontFragmentListener.onTextStyle(4); // Normal
        }
    }

    private List<String> loadFontList() {
        return Arrays.asList(
                "font1.ttf", "font2.ttf", "font3.ttf", "font4.TTF", "font5.TTF",
                "font6.ttf", "font7.TTF", "font8.TTF", "font9.TTF", "font10.TTF",
                "font11.otf", "font12.ttf", "font13.ttf", "font14.TTF", "font15.TTF",
                "font16.ttf", "font17.otf", "font18.otf", "font19.otf", "font20.otf",
                "font21.otf", "font22.otf", "font23.ttf", "font25.otf", "font26.otf",
                "font27.otf", "font28.ttf", "font29.ttf", "font30.otf", "font31.otf",
                "font32.otf", "font33.ttf", "font34.ttf", "font35.ttf"
        );
    }
}
