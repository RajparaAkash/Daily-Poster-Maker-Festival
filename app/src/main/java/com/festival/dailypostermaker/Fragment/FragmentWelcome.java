package com.festival.dailypostermaker.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.festival.dailypostermaker.Model.Welcome;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.FragmentWelcomeBinding;

import java.util.ArrayList;

public class FragmentWelcome extends Fragment {

    public Context context;
    private FragmentWelcomeBinding binding;

    public FragmentWelcome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = requireContext();
        int position = 0;
        if (getArguments() != null) {
            position = getArguments().getInt("position");
        }

        ArrayList<Welcome> welcomes = new ArrayList<>();
        welcomes.add(new Welcome(R.drawable.welcome_img_01, context.getString(R.string.intro_des_01)));
        welcomes.add(new Welcome(R.drawable.welcome_img_02, context.getString(R.string.intro_des_02)));
        welcomes.add(new Welcome(R.drawable.welcome_img_03, context.getString(R.string.intro_des_03)));

        binding.ivIntro.setImageResource(welcomes.get(position).getIcon());
        binding.tvTitle.setText(welcomes.get(position).getTitle());
    }
}