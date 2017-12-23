package com.george.euzin;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.george.euzin.data.EuZinContract;
import com.george.euzin.data.EuZinMainGridDbHelper;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.IOException;

public class ObservableActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,ObservableScrollViewCallbacks {

    private SunScreen mSun;
    private int number;
    private ImageView imageO;
    private TextView textO,mTitleView;
    private static final String TABLE_TO_PASS = "table_pass";
    private String tableToQuery;
    private static final int MAIN_LOADER = 74;
    private View overlay;
    private ObservableScrollView oScrollView;
    private FloatingActionButton fab;
    private int mFlexibleSpaceImageHeight,mFlexibleSpaceShowFabOffset,mFabMargin,mActionBarSize,actionBarHeight;
    private ActionBar ab;
    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private SQLiteDatabase mDb;
    private EuZinMainGridDbHelper dbHelper;
    private String rowForHeart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flexiblespacewithimagescrollview);

        //Getting the number clicked from previous Activity
        Intent intent = getIntent();
        if (intent.hasExtra(mSun.NUMBER_OF_LIST)) {
            number = intent.getIntExtra(mSun.NUMBER_OF_LIST, 0);
            Log.e("ObserveActivity", String.valueOf(number));
        }

        //Getting the Table used from the previous activity
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        tableToQuery = sharedPreferences.getString(TABLE_TO_PASS, "detailTable");
        Log.e("ObserveActivityTABLE", tableToQuery);

        imageO = (ImageView) findViewById(R.id.imageObservable);
        textO = (TextView) findViewById(R.id.titleText);
        overlay= findViewById(R.id.overlay);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        mTitleView = (TextView)findViewById(R.id.titleText);

        ViewHelper.setScaleX(fab, 0);
        ViewHelper.setScaleY(fab, 0);
        ab = getSupportActionBar();
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        mActionBarSize = actionBarHeight;

        oScrollView = (ObservableScrollView)findViewById(R.id.oScroll);
        oScrollView.setScrollViewCallbacks(this);

        ScrollUtils.addOnGlobalLayoutListener(oScrollView, new Runnable() {
            @Override
            public void run() {
                oScrollView.scrollTo(0, 1);

                // If you'd like to start from scrollY == 0, don't write like this:
                //mScrollView.scrollTo(0, 0);
                // The initial scrollY is 0, so it won't invoke onScrollChanged().
                // To do this, use the following:
                //onScrollChanged(0, false, false);

                // You can also achieve it with the following codes.
                // This causes scroll change from 1 to 0.
                //mScrollView.scrollTo(0, 1);
                //mScrollView.scrollTo(0, 0);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageView imageView = (ImageView) view;
                assert(R.id.imageObservable == imageView.getId());

                // See here
                Integer integer = (Integer) imageView.getTag();
                integer = integer == null ? 0 : integer;

                switch(integer) {
                    case R.drawable.heart_in:
                        imageView.setImageResource(R.drawable.heart_out);
                        imageView.setTag(R.drawable.heart_out);

                        makeHeartEmpty();
                        Log.e("OnClick","Out");

                        break;
                    case R.drawable.heart_out:
                    default:
                        imageView.setImageResource(R.drawable.heart_in);
                        imageView.setTag(R.drawable.heart_in);

                        makeHeartFull();
                        Log.e("OnClick","In");

                        break;
                }
            }
        });

        //Getting the database from EuZinMainDbHelper
        try {
            dbHelper = new EuZinMainGridDbHelper(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mDb = dbHelper.getWritableDatabase();

        getSupportLoaderManager().initLoader(MAIN_LOADER,null,this);

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor cursor;

            @Override
            protected void onStartLoading() {

                if (cursor != null) {
                    deliverResult(cursor);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    Cursor mCursor = mDb.query(tableToQuery,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
                    return mCursor;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                cursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToPosition(number);
        String text = data.getString(data.getColumnIndex(EuZinContract.DetailView.DETAIL_VIEW_TITLE_TEXT));
        textO.setText(text);

        byte[] image = data.getBlob(data.getColumnIndex(EuZinContract.DetailView.DETAIL_VIEW_IMAGE));
        Bitmap bitmap = getImage(image);
        imageO.setImageBitmap(bitmap);

        int heartNumber = data.getInt(data.getColumnIndex(EuZinContract.DetailView.DETAIL_VIEW_HEART));
        if(heartNumber==0){
            fab.setImageResource(R.drawable.heart_out);
            Log.e("HeartOut","Done");
        }else if(heartNumber==1){
            fab.setImageResource(R.drawable.heart_in);
            Log.e("HeartIn","DoneDoneeeeee");
        }

        int rowId = data.getInt(data.getColumnIndex(EuZinContract.DetailView._ID));
        rowForHeart = String.valueOf(rowId);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    // convert from byte array to bitmap
    private static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - overlay.getHeight();
        ViewHelper.setTranslationY(overlay, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(overlay, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        ViewHelper.setAlpha(overlay, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        int maxFabTranslationY = mFlexibleSpaceImageHeight - fab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - fab.getHeight() / 2,
                mActionBarSize - fab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fab.getLayoutParams();
            lp.leftMargin = overlay.getWidth() - mFabMargin - fab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            fab.requestLayout();
        } else {
            ViewHelper.setTranslationX(fab, fab.getWidth() - mFabMargin - fab.getWidth());
            ViewHelper.setTranslationY(fab, fabTranslationY);
        }

        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    private boolean mFabIsShown;

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(fab).cancel();
            ViewPropertyAnimator.animate(fab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(fab).cancel();
            ViewPropertyAnimator.animate(fab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }

    private void makeHeartFull(){

        ContentValues cv = new ContentValues();
        cv.put(EuZinContract.DetailView.DETAIL_VIEW_HEART,1);
        mDb.update(tableToQuery,cv,"_id = ?", new String[]{rowForHeart});
        Log.e("FullUpdate",tableToQuery+"-"+ number);
    }

    private void makeHeartEmpty(){

        ContentValues cv = new ContentValues();
        cv.put(EuZinContract.DetailView.DETAIL_VIEW_HEART,0);
        mDb.update(tableToQuery,cv,"_id = ?", new String[]{rowForHeart});
        Log.e("EmptyUpdate",tableToQuery+"-"+ number);
    }
}
