package com.festival.dailypostermaker.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.festival.dailypostermaker.Model.Bg.BgSub;
import com.festival.dailypostermaker.R;

import java.io.File;
import java.util.ArrayList;

public class AdapterBgDialogDownloaded extends RecyclerView.Adapter<AdapterBgDialogDownloaded.ViewHolder> {

    private final Activity activity;
    private final BottomSheetDialog bgDialog;
    private final ArrayList<BgSub> backgroundList;
    private final Click click;

    public AdapterBgDialogDownloaded(Activity activity, BottomSheetDialog bgDialog, ArrayList<BgSub> backgroundList, Click click) {
        this.activity = activity;
        this.bgDialog = bgDialog;
        this.backgroundList = backgroundList;
        this.click = click;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bg_dialog_downloaded, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BgSub background = backgroundList.get(position);

        holder.bgCategoryTxt.setText(background.getName());

        AdapterBgDialogSub adapterBgDialogSub = new AdapterBgDialogSub(activity, bgDialog, background.getMediaPaths());
        holder.subBgRv.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        holder.subBgRv.setAdapter(adapterBgDialogSub);

        holder.deleteBgCatImg.setOnClickListener(view -> {
            new AlertDialog.Builder(activity)
                    .setMessage(R.string.are_you_sure_you_want_to_delete)
                    .setPositiveButton(R.string.delete, (dialog, which) -> {
                        deleteFolder(new File(background.getPath()));
                        backgroundList.remove(position);
                        notifyDataSetChanged();
                        click.onDeleteClick(backgroundList.size());
                        Toast.makeText(activity, R.string.deleted_successfully, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        });
    }

    public interface Click {
        void onDeleteClick(int size);
    }

    private boolean deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFolder(file);
                }
            }
        }
        return folder.delete();
    }

    @Override
    public int getItemCount() {
        return backgroundList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView bgCategoryTxt;
        RecyclerView subBgRv;
        ImageView deleteBgCatImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bgCategoryTxt = itemView.findViewById(R.id.bgCategoryTxt);
            subBgRv = itemView.findViewById(R.id.subBgRv);
            deleteBgCatImg = itemView.findViewById(R.id.deleteBgCatImg);
        }
    }
}
