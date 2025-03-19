package com.festival.dailypostermaker.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.festival.dailypostermaker.Model.Downloads;
import com.festival.dailypostermaker.MyUtils.FileUtils;
import com.festival.dailypostermaker.R;

import java.util.ArrayList;

public class AdapterDownloads extends RecyclerView.Adapter<AdapterDownloads.ViewHolder> {

    private final Context context;
    private final ArrayList<Downloads> downloadsList;
    private final Click click;

    public AdapterDownloads(Activity context, ArrayList<Downloads> downloadsList, Click click) {
        this.context = context;
        this.downloadsList = downloadsList;
        this.click = click;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_downloads, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String mediaPath = downloadsList.get(position).getFilePath();

        if (FileUtils.isImage(mediaPath)) {
            Glide.with(context)
                    .load(mediaPath)
                    .placeholder(R.drawable.img_place_holder)
                    .placeholder(R.drawable.img_place_holder)
                    .into(holder.thumbnailImg);
        }

        holder.itemView.setOnClickListener(view -> {
            click.onClick(holder.getAdapterPosition(), mediaPath);
        });
    }

    public interface Click {
        void onClick(int pos, String path);
    }

    public ArrayList<Downloads> getMedia() {
        return this.downloadsList;
    }

    @Override
    public int getItemCount() {
        if (downloadsList == null)
            return 0;
        return downloadsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView thumbnailImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImg = itemView.findViewById(R.id.thumbnailImg);
        }
    }
}