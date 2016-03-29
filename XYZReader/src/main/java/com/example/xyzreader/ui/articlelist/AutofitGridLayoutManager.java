package com.example.xyzreader.ui.articlelist;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by geo on 3/24/16.
 */
public class AutofitGridLayoutManager extends GridLayoutManager {
    /**
     * the number of columns in the grid depends on how much width we've got to play with
     */
    private int mBreakPoint;

    public AutofitGridLayoutManager(Context context, int breakPoint) {
        super (context, 1);
        mBreakPoint = breakPoint;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        int spanCount = getWidth() / mBreakPoint;
        if (spanCount < 1) {
            spanCount = 1;
        }
        setSpanCount(spanCount);
        super.onLayoutChildren(recycler, state);
    }
}