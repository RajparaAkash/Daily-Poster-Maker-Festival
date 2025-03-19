package com.festival.dailypostermaker.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.makeramen.roundedimageview.RoundedImageView;
import com.festival.dailypostermaker.Model.PostDetails;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.R;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;

public class AdapterPostCreate extends DiscreteScrollView.Adapter<AdapterPostCreate.ViewHolder> {

    private final Context context;
    private final ArrayList<PostDetails> postDetailsList;
    private final String tempURL;

    public AdapterPostCreate(Context context, ArrayList<PostDetails> postDetailsList, String tempURL) {
        this.context = context;
        this.postDetailsList = postDetailsList;
        this.tempURL = tempURL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(postDetailsList.get(viewType).getLayoutList(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(tempURL)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        Palette.from(((BitmapDrawable) resource).getBitmap()).generate(palette -> {
                            Palette.Swatch dominantSwatch = palette.getDominantSwatch();
                            if (dominantSwatch != null) {
                                int dominantColor = palette.getMutedColor(ContextCompat.getColor(context, R.color.defaultPostBgColor));
                                boolean isDark = isColorDark(dominantColor);
                                if (isDark) {
                                    holder.tvTitle.setTextColor(Color.WHITE);
                                    holder.tvDes.setTextColor(Color.WHITE);
                                    holder.tvWhatsapp.setTextColor(Color.WHITE);
                                    holder.tvAddress.setTextColor(Color.WHITE);
                                    holder.tvSocialMedia.setTextColor(Color.WHITE);
                                } else {
                                    holder.tvTitle.setTextColor(Color.BLACK);
                                    holder.tvDes.setTextColor(Color.BLACK);
                                    holder.tvWhatsapp.setTextColor(Color.BLACK);
                                    holder.tvAddress.setTextColor(Color.BLACK);
                                    holder.tvSocialMedia.setTextColor(Color.BLACK);
                                }
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        if (MyPreference.get_IsProfileBusiness()) {
            holder.tvTitle.setText(MyPreference.get_B_Name());
            holder.tvDes.setText(MyPreference.get_B_Designation());
            holder.tvWhatsapp.setText(MyPreference.get_B_Whatsapp());
            holder.tvAddress.setText(MyPreference.get_B_Address());

            if (MyPreference.get_B_SelectedSocialMedia() == 1) {
                if (!TextUtils.isEmpty(MyPreference.get_B_Instagram())) {
                    holder.imgSocialMedia.setImageResource(R.drawable.icon_edit_profile_instagram);
                    holder.tvSocialMedia.setText(MyPreference.get_B_Instagram());
                } else {
                    holder.imgSocialMedia.setVisibility(View.GONE);
                    holder.tvSocialMedia.setVisibility(View.GONE);
                }

            } else if (MyPreference.get_B_SelectedSocialMedia() == 2) {
                if (!TextUtils.isEmpty(MyPreference.get_B_YouTube())) {
                    holder.imgSocialMedia.setImageResource(R.drawable.icon_edit_profile_youtube);
                    holder.tvSocialMedia.setText(MyPreference.get_B_YouTube());
                } else {
                    holder.imgSocialMedia.setVisibility(View.GONE);
                    holder.tvSocialMedia.setVisibility(View.GONE);
                }

            } else if (MyPreference.get_B_SelectedSocialMedia() == 3) {
                if (!TextUtils.isEmpty(MyPreference.get_B_Facebook())) {
                    holder.imgSocialMedia.setImageResource(R.drawable.icon_edit_profile_facebook);
                    holder.tvSocialMedia.setText(MyPreference.get_B_Facebook());
                } else {
                    holder.imgSocialMedia.setVisibility(View.GONE);
                    holder.tvSocialMedia.setVisibility(View.GONE);
                }

            } else if (MyPreference.get_B_SelectedSocialMedia() == 4) {
                if (!TextUtils.isEmpty(MyPreference.get_B_Twitter())) {
                    holder.imgSocialMedia.setImageResource(R.drawable.icon_edit_profile_twitter);
                    holder.tvSocialMedia.setText(MyPreference.get_B_Twitter());
                } else {
                    holder.imgSocialMedia.setVisibility(View.GONE);
                    holder.tvSocialMedia.setVisibility(View.GONE);
                }

            } else if (MyPreference.get_B_SelectedSocialMedia() == 5) {
                if (!TextUtils.isEmpty(MyPreference.get_B_Email())) {
                    holder.imgSocialMedia.setImageResource(R.drawable.icon_edit_profile_email);
                    holder.tvSocialMedia.setText(MyPreference.get_B_Email());
                } else {
                    holder.imgSocialMedia.setVisibility(View.GONE);
                    holder.tvSocialMedia.setVisibility(View.GONE);
                }

            } else if (MyPreference.get_B_SelectedSocialMedia() == 6) {
                if (!TextUtils.isEmpty(MyPreference.get_B_Website())) {
                    holder.imgSocialMedia.setImageResource(R.drawable.icon_edit_profile_website);
                    holder.tvSocialMedia.setText(MyPreference.get_B_Website());
                } else {
                    holder.imgSocialMedia.setVisibility(View.GONE);
                    holder.tvSocialMedia.setVisibility(View.GONE);
                }
            }

            if (MyPreference.get_B_ProfilePath().isEmpty()) {
                holder.logoImg.setImageResource(R.drawable.icon_dummy_business_02);
            } else {
                Glide.with(context)
                        .load(MyPreference.get_B_ProfilePath())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.img_place_holder)
                        .into(holder.logoImg);
            }

        } else {
            holder.tvTitle.setText(MyPreference.get_P_Name());
            holder.tvDes.setText(MyPreference.get_P_About());
            holder.tvWhatsapp.setText(MyPreference.get_P_MobileNo());
            holder.tvAddress.setText(MyPreference.get_P_Address());

            if (MyPreference.get_P_SelectedSocialMedia() == 1) {
                if (!TextUtils.isEmpty(MyPreference.get_P_Instagram())) {
                    holder.imgSocialMedia.setImageResource(R.drawable.icon_edit_profile_instagram);
                    holder.tvSocialMedia.setText(MyPreference.get_P_Instagram());
                } else {
                    holder.imgSocialMedia.setVisibility(View.GONE);
                    holder.tvSocialMedia.setVisibility(View.GONE);
                }

            } else if (MyPreference.get_P_SelectedSocialMedia() == 2) {
                if (!TextUtils.isEmpty(MyPreference.get_P_YouTube())) {
                    holder.imgSocialMedia.setImageResource(R.drawable.icon_edit_profile_youtube);
                    holder.tvSocialMedia.setText(MyPreference.get_P_YouTube());
                } else {
                    holder.imgSocialMedia.setVisibility(View.GONE);
                    holder.tvSocialMedia.setVisibility(View.GONE);
                }

            } else if (MyPreference.get_P_SelectedSocialMedia() == 3) {
                if (!TextUtils.isEmpty(MyPreference.get_P_Facebook())) {
                    holder.imgSocialMedia.setImageResource(R.drawable.icon_edit_profile_facebook);
                    holder.tvSocialMedia.setText(MyPreference.get_P_Facebook());
                } else {
                    holder.imgSocialMedia.setVisibility(View.GONE);
                    holder.tvSocialMedia.setVisibility(View.GONE);
                }

            } else if (MyPreference.get_P_SelectedSocialMedia() == 4) {
                if (!TextUtils.isEmpty(MyPreference.get_P_Twitter())) {
                    holder.imgSocialMedia.setImageResource(R.drawable.icon_edit_profile_twitter);
                    holder.tvSocialMedia.setText(MyPreference.get_P_Twitter());
                } else {
                    holder.imgSocialMedia.setVisibility(View.GONE);
                    holder.tvSocialMedia.setVisibility(View.GONE);
                }

            } else if (MyPreference.get_P_SelectedSocialMedia() == 5) {
                if (!TextUtils.isEmpty(MyPreference.get_P_Email())) {
                    holder.imgSocialMedia.setImageResource(R.drawable.icon_edit_profile_email);
                    holder.tvSocialMedia.setText(MyPreference.get_P_Email());
                } else {
                    holder.imgSocialMedia.setVisibility(View.GONE);
                    holder.tvSocialMedia.setVisibility(View.GONE);
                }

            } else if (MyPreference.get_P_SelectedSocialMedia() == 6) {
                if (!TextUtils.isEmpty(MyPreference.get_P_Website())) {
                    holder.imgSocialMedia.setImageResource(R.drawable.icon_edit_profile_website);
                    holder.tvSocialMedia.setText(MyPreference.get_P_Website());
                } else {
                    holder.imgSocialMedia.setVisibility(View.GONE);
                    holder.tvSocialMedia.setVisibility(View.GONE);
                }
            }

            if (MyPreference.get_P_ProfilePath().isEmpty()) {
                holder.logoImg.setImageResource(R.drawable.icon_dummy_personal_02);
            } else {
                Glide.with(context)
                        .load(MyPreference.get_P_ProfilePath())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .placeholder(R.drawable.img_place_holder)
                        .into(holder.logoImg);
            }
        }
    }

    private boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness >= 0.3;
    }

    @Override
    public int getItemCount() {
        return postDetailsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvDes;
        private final TextView tvWhatsapp;
        private final TextView tvAddress;
        private final RoundedImageView logoImg;
        private final ImageView imgSocialMedia;
        private final TextView tvSocialMedia;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDes = itemView.findViewById(R.id.tvDes);
            tvWhatsapp = itemView.findViewById(R.id.tvWhatsapp);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            logoImg = itemView.findViewById(R.id.logoImg);
            imgSocialMedia = itemView.findViewById(R.id.imgSocialMedia);
            tvSocialMedia = itemView.findViewById(R.id.tvSocialMedia);
        }
    }
}