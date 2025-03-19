package com.festival.dailypostermaker.MyUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

public abstract class RvScrollListener extends OnScrollListener {

    private int current_page = 1;
    private boolean loading = true;
    private final LinearLayoutManager mLinearLayoutManager;
    private int previousTotal = 0;

    public abstract void onLoadMore(int i);

    protected RvScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = this.mLinearLayoutManager.getItemCount();
        int firstVisibleItem = this.mLinearLayoutManager.findFirstVisibleItemPosition();
        if (visibleItemCount == 0) {
            this.previousTotal = 0;
            this.loading = true;
        }
        if (this.loading && totalItemCount > this.previousTotal + 1) {
            this.loading = false;
            this.previousTotal = totalItemCount;
        }
        int visibleThreshold = 1;
        if (!this.loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            this.current_page++;
            onLoadMore(this.current_page);
            this.loading = true;
        }
    }
}
