package com.festival.dailypostermaker.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.R;

import java.util.Arrays;
import java.util.List;

public class AdapterEditColorShadow extends RecyclerView.Adapter<AdapterEditColorShadow.ColorViewHolder> {

    private final ColorAdapterListener colorAdapterListener;
    private final Activity activity;
    private final List<Integer> colorList;

    public interface ColorAdapterListener {
        void onColorItemSelected(int color);
    }

    public AdapterEditColorShadow(Activity activity, ColorAdapterListener colorAdapterListener) {
        this.activity = activity;
        this.colorAdapterListener = colorAdapterListener;
        this.colorList = generateColorList();
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_color_edit_post, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        holder.noneTxt.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        holder.colorSelectImg.setVisibility(MyUtil.edit_shadow_color_pos == position ? View.VISIBLE : View.GONE);
        holder.colorSection.setBackgroundColor(colorList.get(position));
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {
        final FrameLayout colorSection;
        final TextView noneTxt;
        final AppCompatImageView colorSelectImg;

        public ColorViewHolder(View itemView) {
            super(itemView);
            colorSection = itemView.findViewById(R.id.color_section);
            noneTxt = itemView.findViewById(R.id.noneTxt);
            colorSelectImg = itemView.findViewById(R.id.colorSelectImg);

            // Set click listener for each color item
            itemView.setOnClickListener(view -> {
                if (getAdapterPosition() != -1 && getAdapterPosition() < colorList.size()) {
                    colorAdapterListener.onColorItemSelected(colorList.get(getAdapterPosition()));
                    MyUtil.edit_shadow_color_pos = getAdapterPosition();
                    notifyDataSetChanged();
                }
            });
        }
    }

    private List<Integer> generateColorList() {
        return Arrays.asList(
                Color.parseColor("#F3F2F2"), Color.parseColor("#000000"), Color.parseColor("#0098F1"),
                Color.parseColor("#4CC259"), Color.parseColor("#FFC859"), Color.parseColor("#FF8523"),
                Color.parseColor("#FF3A4A"), Color.parseColor("#E90060"), Color.parseColor("#B300B6"),
                Color.parseColor("#FF0000"), Color.parseColor("#FF7E88"), Color.parseColor("#FFD0D1"),
                Color.parseColor("#FFDAB2"), Color.parseColor("#FFC07E"), Color.parseColor("#E18B42"),
                Color.parseColor("#a36138"), Color.parseColor("#4A2829"), Color.parseColor("#004C30"),
                Color.parseColor("#2C2C2C"), Color.parseColor("#393939"), Color.parseColor("#555555"),
                Color.parseColor("#727272"), Color.parseColor("#989898"), Color.parseColor("#B1B1B1"),
                Color.parseColor("#C7C7C7"), Color.parseColor("#DBDBDB"), Color.parseColor("#F0F0F0")
        );
    }
}
