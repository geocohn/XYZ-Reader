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

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.UpdaterService;

/**
 * A placeholder fragment containing a recycler view inside of a swipe refresher.
 */
public class ArticleListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private Context mContext;
    private RecyclerView mRecyclerView;
    private BroadcastReceiver mRefreshingReceiver;
    private SwipeRefreshLayout mSwiper;

    public ArticleListFragment() {
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        mContext = context;
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_article_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.article_list);
        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);

        mContext.startService(new Intent(mContext, UpdaterService.class));

        mSwiper = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);
        mSwiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mContext.startService(new Intent(mContext, UpdaterService.class));
            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mRefreshingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                    boolean updaterIsRefreshing =
                            intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                    if (!updaterIsRefreshing) {
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        if (mSwiper != null) {
                            mSwiper.setRefreshing(false);
                        }
                    }
                }
            }
        };
        mContext.registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    public void onStop() {
        super.onStop();
        mContext.unregisterReceiver(mRefreshingReceiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Log.d(LOG_TAG, "creating loader");
        return ArticleLoader.newAllArticlesInstance(mContext);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArticleListAdapter adapter = (ArticleListAdapter) mRecyclerView.getAdapter();
        adapter.changeCursor(data);
        adapter.notifyDataSetChanged();
//        Log.d(LOG_TAG, "load finished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((ArticleListAdapter)(mRecyclerView.getAdapter())).changeCursor(null);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        // derive the maximum column width from the dimens xml
        int breakpoint = (int) getResources().getDimension(R.dimen.max_listitem_width);

        recyclerView.setLayoutManager(new AutofitGridLayoutManager(mContext, breakpoint));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ArticleListAdapter(mContext, null));
    }
}
