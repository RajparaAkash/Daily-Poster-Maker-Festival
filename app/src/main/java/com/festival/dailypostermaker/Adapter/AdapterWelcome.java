package com.festival.dailypostermaker.Adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.festival.dailypostermaker.Fragment.FragmentWelcome;

public class AdapterWelcome extends FragmentStateAdapter {

    public AdapterWelcome(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        FragmentWelcome itemFragment = new FragmentWelcome();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        itemFragment.setArguments(bundle);
        return itemFragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
