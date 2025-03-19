package com.festival.dailypostermaker.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AdapterBgDownloaded extends RecyclerView.Adapter<AdapterBgDownloaded.ViewHolder> {

    private final Activity activity;
    private final ArrayList<String> mediaPaths;

    public AdapterBgDownloaded(Activity activity, ArrayList<String> mediaPaths) {
        this.activity = activity;
        this.mediaPaths = mediaPaths;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bg_downloaded, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(activity.getApplicationContext())
                .load(mediaPaths.get(position))
                .error(R.drawable.img_place_holder)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(holder.backgroundImg);

        holder.itemView.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                downloadImageAndCrop(mediaPaths.get(position));
            }
        });
    }

    private void downloadImageAndCrop(String imageUrl) {
        Glide.with(activity.getApplicationContext())
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Save the downloaded image to the local storage
                        Uri imageUri = saveImageToLocal(resource);

                        // Start UCrop activity
                        startUCrop(imageUri);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle the placeholder
                    }
                });
    }

    private Uri saveImageToLocal(Bitmap bitmap) {
        File file = new File(activity.getCacheDir(), "temp_image.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }

    private void startUCrop(Uri imageUri) {
        Uri destinationUri = Uri.fromFile(new File(activity.getCacheDir(), "cropImage_" + System.currentTimeMillis() + ".png"));
        UCrop.Options options = new UCrop.Options();
        UCrop.of(imageUri, destinationUri)
                .withAspectRatio(1, 1)
                .withOptions(options)
                .start(activity);
    }

    @Override
    public int getItemCount() {
        return mediaPaths.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView backgroundImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            backgroundImg = itemView.findViewById(R.id.backgroundImg);
        }
    }
}
