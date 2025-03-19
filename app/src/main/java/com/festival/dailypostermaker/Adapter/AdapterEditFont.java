package com.festival.dailypostermaker.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.R;

import java.util.List;

public class AdapterEditFont extends RecyclerView.Adapter<AdapterEditFont.FontViewHolder> {

    private final Activity activity;
    private final List<String> fontList;
    private final Click click;

    public AdapterEditFont(Activity activity, List<String> fontList, Click click) {
        this.activity = activity;
        this.fontList = fontList;
        this.click = click;
    }

    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FontViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_font_edit_post, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(FontViewHolder fontViewHolder, @SuppressLint("RecyclerView") int pos) {
        AssetManager assets = this.activity.getAssets();
        TextView textView = fontViewHolder.fontStyleTxt;
        textView.setTypeface(Typeface.createFromAsset(assets, "font/" + fontList.get(pos)));

        if (MyUtil.edit_font_pos == pos) {
            fontViewHolder.fontStyleTxt.setBackgroundResource(R.drawable.bg_font_family_selected);
        } else {
            fontViewHolder.fontStyleTxt.setBackgroundResource(R.drawable.bg_font_family_unselected);
        }

        fontViewHolder.itemView.setOnClickListener(view -> {
            click.onClick(pos);
        });
    }

    public interface Click {
        void onClick(int pos);
    }

    @Override
    public int getItemCount() {
        return fontList.size();
    }

    public static class FontViewHolder extends RecyclerView.ViewHolder {
        TextView fontStyleTxt;

        public FontViewHolder(View view) {
            super(view);
            fontStyleTxt = view.findViewById(R.id.fontStyleTxt);
        }
    }
}
