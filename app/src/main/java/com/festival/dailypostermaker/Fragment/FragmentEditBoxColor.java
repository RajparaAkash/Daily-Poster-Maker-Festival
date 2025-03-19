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
import com.festival.dailypostermaker.Adapter.AdapterEditColorBox;
import com.festival.dailypostermaker.Interface.HightLightFragmentListener;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.databinding.FragmentEditBoxcolorBinding;

public class FragmentEditBoxColor extends Fragment implements AdapterEditColorBox.ColorAdapterListener {

    private AdapterEditColorBox adapterEditColorBox;
    private HightLightFragmentListener hightLightFragmentListener;
    private String pos = "28";

    private FragmentEditBoxcolorBinding binding;

    public FragmentEditBoxColor() {
    }

    public void setListener(HightLightFragmentListener hightLightFragmentListener) {
        this.hightLightFragmentListener = hightLightFragmentListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        binding = FragmentEditBoxcolorBinding.inflate(layoutInflater, viewGroup, false);

        setupRecyclerView();
        setupSeekBar();
        setupColorPicker();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.boxColorRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapterEditColorBox = new AdapterEditColorBox(getActivity(), this);
        binding.boxColorRv.setAdapter(adapterEditColorBox);
    }

    private void setupSeekBar() {
        binding.fontBgOpacitySeekbar.setProgress(0);
        binding.fontBgOpacitySeekbar.setMax(255);
        binding.fontBgOpacitySeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                pos = (i < 16) ? changeProgress(i) : Integer.toHexString(i);
                if (hightLightFragmentListener != null) {
                    hightLightFragmentListener.onHightLightColorOpacityChangeListerner(pos);
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
                        if (hightLightFragmentListener != null) {
                            hightLightFragmentListener.onHightLightColorSelected(color);
                        }

                        if (adapterEditColorBox != null) {
                            MyUtil.edit_box_color_pos = -1;
                            adapterEditColorBox.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    })
                    .build().show();
        });
    }

    private String changeProgress(int i) {
        return (i >= 0 && i <= 15) ? String.format("%02X", i) : null;
    }

    @Override
    public void onColorItemSelected(int i) {
        if (hightLightFragmentListener != null) {
            hightLightFragmentListener.onHightLightColorSelected(i);
        }
    }
}
