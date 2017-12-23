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


public class EuZinDetailAdapter extends RecyclerView.Adapter<EuZinDetailAdapter.DetailViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private euZinDetailClickItemListener mEuZinClickItemListener;

    public interface euZinDetailClickItemListener {
        void onListItemClick(int itemIndex);
    }

    public EuZinDetailAdapter(Context context, euZinDetailClickItemListener listener) {
        mContext = context;
        mEuZinClickItemListener = listener;
    }

    @Override
    public DetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_detail_item, parent, false);
        DetailViewHolder viewHolder = new DetailViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DetailViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        byte[] image = mCursor.getBlob(mCursor.getColumnIndex(EuZinContract.DetailView.DETAIL_VIEW_IMAGE));
        Bitmap bitmap = getImage(image);
        holder.image.setImageBitmap(bitmap);

        String locale = Locale.getDefault().getDisplayLanguage();
        Log.e("Adapter", locale);

        if (locale.equals("Ελληνικά")) {
            String text = mCursor.getString(mCursor.getColumnIndex(EuZinContract.DetailView.DETAIL_VIEW_TITLE_TEXT));
            holder.textTitle.setText(text);

            String text2 = mCursor.getString(mCursor.getColumnIndex(EuZinContract.DetailView.DETAIL_VIEW_PERIGRAFI_TEXT));
            holder.textPerigrafi.setText(text2);
        } else {
            String text3 = mCursor.getString(mCursor.getColumnIndex(EuZinContract.DetailView.DETAIL_VIEW_TITLE_ENGLISH));
            holder.textTitle.setText(text3);

            String text4 = mCursor.getString(mCursor.getColumnIndex(EuZinContract.DetailView.DETAIL_VIEW_PERIGRAFI_ENGLISH));
            holder.textPerigrafi.setText(text4);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    class DetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView textTitle, textPerigrafi;

        public DetailViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.detailImage);
            textTitle = (TextView) itemView.findViewById(R.id.detailTextTitle);
            textPerigrafi = (TextView) itemView.findViewById(R.id.detailTextPerigrafi);
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
