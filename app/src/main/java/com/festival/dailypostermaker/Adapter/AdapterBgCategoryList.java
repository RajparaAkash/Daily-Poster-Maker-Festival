package com.festival.dailypostermaker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.festival.dailypostermaker.Model.Bg.BgSub;
import com.festival.dailypostermaker.R;

import java.util.ArrayList;

public class AdapterBgCategoryList extends RecyclerView.Adapter<AdapterBgCategoryList.ViewHolder> {

    private final Context context;
    private final ArrayList<BgSub> categoryBgList;
    private final Click click;

    public AdapterBgCategoryList(Context context, ArrayList<BgSub> categoryBgList, Click click) {
        this.context = context;
        this.categoryBgList = categoryBgList;
        this.click = click;

        if (!categoryBgList.isEmpty()) {
            for (BgSub c : categoryBgList) {
                c.setSelected(false);
            }
            categoryBgList.get(0).setSelected(true);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bg_category_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BgSub category = categoryBgList.get(position);
        holder.tvCategoryName.setText(category.getName());

        if (category.isSelected()) {
            holder.itemView.setBackgroundResource(R.drawable.bg_50dp_gradient_primary);
            holder.tvCategoryName.setTextColor(context.getColor(R.color.white));
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_50dp_gray);
            holder.tvCategoryName.setTextColor(context.getColor(R.color.textColor));
        }

        holder.itemView.setOnClickListener(v -> {
            for (BgSub c : categoryBgList) {
                c.setSelected(false);
            }
            category.setSelected(true);
            notifyDataSetChanged();

            click.onCategoryClick(position);
        });
    }

    public interface Click {
        void onCategoryClick(int pos);
    }

    @Override
    public int getItemCount() {
        return categoryBgList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
