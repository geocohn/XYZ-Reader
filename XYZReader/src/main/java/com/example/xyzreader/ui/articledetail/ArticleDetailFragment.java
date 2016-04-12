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

package com.example.xyzreader.ui.articledetail;

/**
 * Created by geo on 3/30/16.
 * A placeholder fragment containing a simple view.
 */

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARTICLE_ID = "article id";

    private final String LOG_TAG = this.getClass().getSimpleName();

    private Context mContext;
    private long mArticleId;
    private Cursor mCursor;
    private View mRootView;
    private Palette mPalette;

    public ArticleDetailFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ArticleDetailFragment newInstance(long articleId) {
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARTICLE_ID, articleId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArticleId = getArguments().getLong(ARTICLE_ID);
//        Log.d(LOG_TAG, "onCreate Article id = " + mArticleId);
    }

    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.content_article_detail, container, false);


        getLoaderManager().initLoader(0, null, this);
        bindViews();
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
/**
 * The fragments share a FAB and an app bar as part of the activity's collapsing toolbar.
 * Therefore the FAB needs to be hooked up to the fragment every time it's resumed
 * and visible, and the app bar's color can be changed at that time as well
 */
        if (getUserVisibleHint()) {
            setupFab();
            setAppBarColor();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {
            setupFab();
            setAppBarColor();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Log.d(LOG_TAG, "onCreateLoader (" + id + ", args), mArticleId = " + mArticleId);
        return ArticleLoader.newInstanceForItemId(mContext, mArticleId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!isAdded()) {
            if (data != null) {
                data.close();
            }
            return;
        }

        mCursor = data;
        if (mCursor != null && !mCursor.moveToFirst()) {
//            Log.e(LOG_TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
        bindViews();
    }

    private void setupFab() {
        if (mContext == null) {
            return;
        }
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendArticle();
            }
        });
    }

    private void sendArticle() {
        if (mCursor != null) {
            String title = mCursor.getString(ArticleLoader.Query.TITLE);
            String byline = ((TextView) mRootView.findViewById(R.id.article_detail_byline))
                    .getText().toString();
            Spanned textBody = Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY));
            String photoUrl = mCursor.getString(ArticleLoader.Query.PHOTO_URL);

            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            }
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/*");
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.putExtra(Intent.EXTRA_TEXT, title
                    + "\n"
                    + byline
                    + "\n\n"
                    + photoUrl
                    + "\n\n"
                    + textBody
                    + "\n\n#"
                    + mContext.getString(R.string.app_name));
            ShareActionProvider shareActionProvider = new ShareActionProvider(mContext);
            shareActionProvider.setShareIntent(intent);
            startActivity(intent);
        }
    }

    private void setAppBarColor() {
        if (mContext != null && mPalette != null) {
//            Log.d(LOG_TAG, "setting up appbar");
            getActivity().findViewById(R.id.toolbar_layout)
                    .setBackgroundColor(mPalette.getVibrantColor(
                            ContextCompat.getColor(mContext, R.color.theme_primary)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().
                        setStatusBarColor(mPalette.getDarkMutedColor(
                                ContextCompat.getColor(mContext, R.color.theme_primary_dark)));
            }
        }
    }

    private void bindViews() {
        if (mRootView == null || mCursor == null) {
            return;
        }
        final ImageView detailImageView = (ImageView) mRootView.findViewById(R.id.detail_image);
        final TextView articleTitle = (TextView) mRootView.findViewById(R.id.article_detail_title);
        final TextView articleByLine = (TextView) mRootView.findViewById(R.id.article_detail_byline);
        final TextView bodyView = (TextView) mRootView.findViewById(R.id.article_body);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            long itemId = mCursor.getLong(ArticleLoader.Query._ID);
            String imageTransitionName = mContext.getString(R.string.image_transition_name) + itemId;
            detailImageView.setTransitionName(imageTransitionName);
//            Log.d(LOG_TAG, "detail image transition name = '" + imageTransitionName + "'");
        }

        Picasso.with(mContext)
                .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
                .into(detailImageView, new Callback() {
                    @Override
                    public void onSuccess() {
//                        Log.d(LOG_TAG, "successful image load");
                        mPalette = generatePalette(detailImageView);
                        if (getUserVisibleHint() && isResumed()) {
                            setAppBarColor();
                        }
                    }

                    @Override
                    public void onError() {
//                        Log.d(LOG_TAG, "image load FAIL");
                    }
                });

        articleTitle.setText(mCursor.getString(ArticleLoader.Query.TITLE));

        String byLine = DateUtils.getRelativeTimeSpanString(
                mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString()
                + " by "
                + mCursor.getString(ArticleLoader.Query.AUTHOR);
        articleByLine.setText(byLine);

        // Light up the embedded links in the body of the article
        bodyView.setMovementMethod(LinkMovementMethod.getInstance());

//        bodyView.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));
        bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));
    }

    private Palette generatePalette(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        return Palette.from(bitmap).generate();
    }

}