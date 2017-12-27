package com.george.euzin;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.george.euzin.data.EuZinContract;

import java.util.Locale;


public class EuZinMainAdapter extends RecyclerView.Adapter<EuZinMainAdapter.MainViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private euZinClickItemListener mEuZinClickItemListener;

    public interface euZinClickItemListener {
        void onListItemClick(int itemIndex);
    }

    public EuZinMainAdapter(Context context,euZinClickItemListener listener) {
        mContext = context;
        mEuZinClickItemListener = listener;
    }


    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_grid_item, parent, false);

        MainViewHolder vh = new MainViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        //Get image from database
        byte[] image = mCursor.getBlob(mCursor.getColumnIndex(EuZinContract.MainGrid.GRID_IMAGE));
        Bitmap bitmap = getImage(image);
        holder.imageCategories.setImageBitmap(bitmap);

        String locale = Locale.getDefault().getDisplayLanguage();
        Log.e("Adapter", locale);

        //Check if phone is in Greek language
        if (locale.equals("Ελληνικά")) {
            String text = mCursor.getString(mCursor.getColumnIndex(EuZinContract.MainGrid.GRID_TEXT));
            holder.categoriesTextView.setText(text);
        } else {
            String text = mCursor.getString(mCursor.getColumnIndex(EuZinContract.MainGrid.GRID_TEXT_ENGLISH));
            holder.categoriesTextView.setText(text);
        }

    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView categoriesTextView;
        ImageView imageCategories;

        public MainViewHolder(View itemView) {
            super(itemView);

            categoriesTextView = (TextView) itemView.findViewById(R.id.categoriesTextView);
            imageCategories = (ImageView) itemView.findViewById(R.id.imageCategories);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mEuZinClickItemListener.onListItemClick(clickedPosition);
        }
    }

    // convert from byte array to bitmap
    private static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public void setCursorData(Cursor cursorData) {
        mCursor = cursorData;
        notifyDataSetChanged();
    }
}
