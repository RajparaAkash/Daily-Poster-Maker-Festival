package com.festival.dailypostermaker.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.festival.dailypostermaker.Activity.ActivitySearchPostData;
import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Model.CategoryName.CategoryItem;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.MyUtils.SingleClickListener;
import com.festival.dailypostermaker.R;

import java.util.ArrayList;

public class AdapterSearchCategoryRecent extends RecyclerView.Adapter<AdapterSearchCategoryRecent.ViewHolder> {

    private final Activity activity;
    private final ArrayList<CategoryItem> recentSearchList;

    public AdapterSearchCategoryRecent(Activity activity, ArrayList<CategoryItem> recentSearchList) {
        this.activity = activity;
        this.recentSearchList = recentSearchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_category_recent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem category = recentSearchList.get(position);
        holder.recentSearchCategoryTxt.setText(category.getName());

        holder.itemView.setOnClickListener(new SingleClickListener() {
            @Override
            public void onSingleClick(View view) {
                MyUtil.hideKeyBoard(activity, view);
                MyAdsManager.displayInterstitialSecondWise(activity, () -> {
                    activity.startActivity(new Intent(activity, ActivitySearchPostData.class)
                            .putExtra("CAT_ID", category.getId())
                            .putExtra("CAT_NAME", category.getName()));
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentSearchList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recentSearchCategoryTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recentSearchCategoryTxt = itemView.findViewById(R.id.recentSearchCategoryTxt);
        }
    }
}
