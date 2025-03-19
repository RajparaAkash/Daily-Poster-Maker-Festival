package com.festival.dailypostermaker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.festival.dailypostermaker.Model.CategoryName.CategoryItem;
import com.festival.dailypostermaker.R;

import java.util.ArrayList;

public class AdapterCategoryList extends RecyclerView.Adapter<AdapterCategoryList.ViewHolder> {

    private final Context context;
    private final ArrayList<CategoryItem> categoryList;
    private final CategoryClick categoryClick;

    public AdapterCategoryList(Context context, ArrayList<CategoryItem> categoryList, CategoryClick categoryClick) {
        this.context = context;
        this.categoryList = categoryList;
        this.categoryClick = categoryClick;
        /*if (!categoryList.isEmpty()) {
            categoryList.get(0).setSelected(true);
        }*/
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());

        if (category.isSelected()) {
            holder.itemView.setBackgroundResource(R.drawable.bg_50dp_gradient_primary);
            holder.tvCategoryName.setTextColor(context.getColor(R.color.white));
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_50dp_gray);
            holder.tvCategoryName.setTextColor(context.getColor(R.color.textColor));
        }

        holder.itemView.setOnClickListener(v -> {
            for (CategoryItem c : categoryList) {
                c.setSelected(false);
            }
            category.setSelected(true);
            notifyDataSetChanged();
            categoryClick.onCategoryClick(position, category.getId());
        });
    }

    public interface CategoryClick {
        void onCategoryClick(int pos, int cateID);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
