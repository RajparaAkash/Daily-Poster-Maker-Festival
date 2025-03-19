package com.festival.dailypostermaker.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.festival.dailypostermaker.Activity.ActivitySearchPostData;
import com.festival.dailypostermaker.AdPlacement.MyAdsManager;
import com.festival.dailypostermaker.Model.CategoryName.CategoryItem;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterSearchCategoryList extends RecyclerView.Adapter<AdapterSearchCategoryList.ViewHolder> {

    private final Activity activity;
    private final ArrayList<CategoryItem> categoryList;

    public AdapterSearchCategoryList(Activity activity, ArrayList<CategoryItem> categoryList) {
        this.activity = activity;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_category_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CategoryItem category = categoryList.get(position);
        holder.categoryNameTxt.setText(category.getName());

        Glide.with(activity.getApplicationContext())
                .load(category.getIcon())
                .placeholder(R.drawable.img_place_holder)
                .into(holder.categoryImg);

        holder.itemView.setOnClickListener(v -> {
            MyUtil.hideKeyBoard(activity, v);
            saveCategoryInPreferences(category);
            MyAdsManager.displayInterstitialSecondWise(activity, () -> {
                activity.startActivity(new Intent(activity, ActivitySearchPostData.class)
                        .putExtra("CAT_ID", category.getId())
                        .putExtra("CAT_NAME", category.getName()));
            });
        });
    }

    private void saveCategoryInPreferences(CategoryItem category) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("SearchPreferences", Context.MODE_PRIVATE);
        String categoryJson = sharedPreferences.getString("RecentCategories", "[]");

        try {
            JSONArray jsonArray = new JSONArray(categoryJson);
            // Remove any existing entry with the same name
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("name").equals(category.getName())) {
                    jsonArray.remove(i);
                    break;
                }
            }

            // Add the latest search at the beginning of the list
            JSONObject newJsonObject = new JSONObject();
            newJsonObject.put("id", category.getId());
            newJsonObject.put("name", category.getName());
            jsonArray.put(newJsonObject);

            // Limit the recent searches to a certain number, e.g., 5
            while (jsonArray.length() > 5) {
                jsonArray.remove(0);
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("RecentCategories", jsonArray.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatImageView categoryImg;
        private final TextView categoryNameTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImg = itemView.findViewById(R.id.categoryImg);
            categoryNameTxt = itemView.findViewById(R.id.categoryNameTxt);
        }
    }
}
