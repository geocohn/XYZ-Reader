package com.example.xyzreader.ui.articlelist;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by geo on 3/23/16.
 */
public class ArticleListAdapter  extends CursorRecyclerViewAdapter<ArticleListAdapter.ViewHolder> {

    private Context mContext;
    private RecyclerView.ViewHolder mSelectedHolder = null;
    private int mLastPosition = -1;

    public ArticleListAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.article_list_item, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                        ItemsContract.Items.buildItemUri(getItemId(vh.getAdapterPosition()))));
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {

        // set up the animation per the xml files
        int position = cursor.getPosition();
        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > mLastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        viewHolder.itemView.startAnimation(animation);
        mLastPosition = position;

        //show the thumb image (with progress spinner) or the "not found" image
        viewHolder.mArticleListImageProgressBar.setVisibility(View.VISIBLE);
        Picasso.with(mContext)
                .load(cursor.getString(ArticleLoader.Query.THUMB_URL))
                .into(viewHolder.mArticleListImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.mArticleListImageProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        viewHolder.mArticleListImageView.setImageResource(R.mipmap.ic_launcher);
                    }
                });

        // show the title and byline
        viewHolder.mArticleListItemTitle.setText(cursor.getString(ArticleLoader.Query.TITLE));
        String subTitle = DateUtils.getRelativeTimeSpanString(
                cursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString()
                + " by "
                + cursor.getString(ArticleLoader.Query.AUTHOR);
        viewHolder.mArticleListItemSubtitle.setText(subTitle);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView mArticleListImageView;
        ProgressBar mArticleListImageProgressBar;
        TextView mArticleListItemTitle;
        TextView mArticleListItemSubtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mArticleListImageView = (ImageView) mView.findViewById(R.id.article_list_image);
            //mArticleListImageView.setLayoutParams(new FrameLayout.LayoutParams(400,625));
            mArticleListImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mArticleListImageView.setAdjustViewBounds(true);
            mArticleListImageView.setPadding(8, 8, 8, 8);
            mArticleListImageProgressBar = (ProgressBar) mView.findViewById(R.id.article_list_image_progressbar);
            mArticleListItemTitle = (TextView) mView.findViewById(R.id.article_list_item_title);
            mArticleListItemSubtitle = (TextView) mView.findViewById(R.id.article_list_item_byline);
        }
    }
}
