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

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.festival.dailypostermaker.Adapter.AdapterEditColorText;
import com.festival.dailypostermaker.Interface.ColorFragmentListener;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.databinding.FragmentEditColorBinding;

public class FragmentEditColor extends Fragment implements AdapterEditColorText.ColorAdapterListener {

    private AdapterEditColorText adapterEditColorText;
    private ColorFragmentListener colorFragmentListener;

    private FragmentEditColorBinding binding;

    public FragmentEditColor() {
    }

    public void setListener(ColorFragmentListener colorFragmentListener) {
        this.colorFragmentListener = colorFragmentListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        binding = FragmentEditColorBinding.inflate(layoutInflater, viewGroup, false);

        setupRecyclerView();
        setupSeekBar();
        setupColorPicker();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.fontColorRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapterEditColorText = new AdapterEditColorText(getActivity(), this);
        binding.fontColorRv.setAdapter(adapterEditColorText);
    }

    private void setupSeekBar() {
        binding.fontOpacitySeekbar.setMax(255);
        binding.fontOpacitySeekbar.setProgress(255);
        binding.fontOpacitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (colorFragmentListener != null) {
                    colorFragmentListener.onColorOpacityChangeListerner(i);
                }
            }
        });
    }

    private void setupColorPicker() {
        binding.colorPickerImg.setOnClickListener(view -> {
            ColorPickerDialogBuilder.with(requireActivity())
                    .setTitle("Select color")
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setPositiveButton("OK", (dialogInterface, color, allColors) -> {
                        if (colorFragmentListener != null) {
                            colorFragmentListener.onColorSelected(color);
                        }

                        if (adapterEditColorText != null) {
                            MyUtil.edit_color_pos = -1;
                            adapterEditColorText.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    })
                    .build().show();
        });
    }

    @Override
    public void onColorItemSelected(int i) {
        if (colorFragmentListener != null) {
            colorFragmentListener.onColorSelected(i);
        }
    }
}
