/*
 *
 *  * Copyright (C) 2016 George Cohn III
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.example.xyzreader.ui.articlelist;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by geo on 3/24/16.
 * Rather than specify the number of grid columns using different layouts,
 * use a single breakpoint number to determine the column count
 */
public class AutofitGridLayoutManager extends GridLayoutManager {
    /**
     * the number of columns in the grid depends on how much width we've got to play with
     */
    private final int mBreakPoint;

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