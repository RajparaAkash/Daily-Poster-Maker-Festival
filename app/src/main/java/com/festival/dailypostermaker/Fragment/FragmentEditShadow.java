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
import com.festival.dailypostermaker.Adapter.AdapterEditColorShadow;
import com.festival.dailypostermaker.Interface.ShadowFragmentListener;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.databinding.FragmentEditShadowBinding;

public class FragmentEditShadow extends Fragment implements AdapterEditColorShadow.ColorAdapterListener {

    private AdapterEditColorShadow adapterEditColorShadow;
    private ShadowFragmentListener shadowFragmentListener;

    private FragmentEditShadowBinding binding;

    public FragmentEditShadow() {
    }

    public void setListener(ShadowFragmentListener shadowFragmentListener) {
        this.shadowFragmentListener = shadowFragmentListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        binding = FragmentEditShadowBinding.inflate(layoutInflater, viewGroup, false);

        setupRecyclerView();
        setupSeekBar();
        setupColorPicker();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.shadowColorRv.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        adapterEditColorShadow = new AdapterEditColorShadow(getActivity(), this);
        binding.shadowColorRv.setAdapter(adapterEditColorShadow);
    }

    private void setupSeekBar() {
        binding.shadowSeekbar.setMax(100);
        binding.shadowSeekbar.setProgress(0);
        binding.shadowSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (shadowFragmentListener != null) {
                    shadowFragmentListener.onRadiusChangeListener(i / 10);
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
                    .setPositiveButton("OK", (dialogInterface, color, numArr) -> {
                        if (shadowFragmentListener != null) {
                            shadowFragmentListener.onShadowColorSelected(color);
                        }

                        if (adapterEditColorShadow != null) {
                            MyUtil.edit_shadow_color_pos = -1;
                            adapterEditColorShadow.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    })
                    .build().show();
        });
    }

    @Override
    public void onColorItemSelected(int i) {
        if (shadowFragmentListener != null) {
            shadowFragmentListener.onShadowColorSelected(i);
        }
    }
}
