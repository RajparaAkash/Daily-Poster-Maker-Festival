package com.festival.dailypostermaker.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.festival.dailypostermaker.Interface.FormatTextFragmentListener;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.databinding.FragmentEditAlignBinding;

public class FragmentEditAlign extends Fragment {

    private FormatTextFragmentListener formatTextFragmentListener;

    private FragmentEditAlignBinding binding;

    public FragmentEditAlign() {
    }

    public void setListener(FormatTextFragmentListener formatTextFragmentListener) {
        this.formatTextFragmentListener = formatTextFragmentListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        binding = FragmentEditAlignBinding.inflate(layoutInflater, viewGroup, false);

        setUpBtnClick();

        return binding.getRoot();
    }

    private void setUpBtnClick() {
        binding.alignLeftLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (formatTextFragmentListener != null) {
                    formatTextFragmentListener.onTextAlign(1);
                }
            }
        });

        binding.alignCenterLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (formatTextFragmentListener != null) {
                    formatTextFragmentListener.onTextAlign(2);
                }
            }
        });

        binding.alignRightLay.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                if (formatTextFragmentListener != null) {
                    formatTextFragmentListener.onTextAlign(3);
                }
            }
        });
    }
}
