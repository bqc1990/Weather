package com.bqc1990.weather;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bqc1990.weather.Helper.WeatherDate;
import com.bqc1990.weather.Helper.WeatherPreference;
import com.bqc1990.weather.Helper.WeatherUnit;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private static final String TAG = WeatherAdapter.class.getSimpleName();
    private Cursor mCursor;
    private Context mContext;
    private boolean mUseTodayLayout;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private OnClickMainActivityChangeListener mListener;

    public WeatherAdapter(Context mContext, OnClickMainActivityChangeListener listener) {
        this.mContext = mContext;
        mListener = listener;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId;

        switch (viewType){
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }


            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.weather_list_item;
                break;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        int viewType = getItemViewType(position);
        int weather_id = mCursor.getInt(MainActivity.INDEX_WEATHER_ID);
        int idForWeatherConditionPic = -1;
        String location = WeatherPreference.getPreferredLocation(mContext);
        switch (viewType){
            case VIEW_TYPE_TODAY:
                idForWeatherConditionPic = WeatherUnit.getLargeArtResourceIdForWeatherCondition(weather_id);
                holder.mLocationTextView.setText(location);
                break;
            case VIEW_TYPE_FUTURE_DAY:
                idForWeatherConditionPic = WeatherUnit.getSmallArtResourceIdForWeatherCondition(weather_id);
                break;
        }
        long date = mCursor.getLong(MainActivity.INDEX_DATE);
        String dateString = WeatherDate.dateString(date, position, mContext);
        double max = mCursor.getDouble(MainActivity.INDEX_MAX);
        double min = mCursor.getDouble(MainActivity.INDEX_MIN);
        String weatherCondition = WeatherUnit.getStringForWeatherCondition(mContext, weather_id);
        holder.mWeatherConditionImageView.setImageResource(idForWeatherConditionPic);
        holder.mDescriptionTextView.setText(weatherCondition);
        holder.mDateTextView.setText(dateString);
        holder.mHighTextView.setText(String.valueOf((int) max));
        holder.mLowTextView.setText(String.valueOf((int) min));


    }

    @Override
    public int getItemCount() {
        if(mCursor == null) return 0;
        return mCursor.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (mUseTodayLayout && position == 0) {
            return VIEW_TYPE_TODAY;
        } else {
            return VIEW_TYPE_FUTURE_DAY;
        }
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mWeatherConditionImageView;
        final TextView mDateTextView;
        final TextView mDescriptionTextView;
        final TextView mHighTextView;
        final TextView mLowTextView;
        final TextView mLocationTextView;
        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mDateTextView = itemView.findViewById(R.id.tv_date);
            mWeatherConditionImageView = itemView.findViewById(R.id.iv_weather_condition);
            mDescriptionTextView = itemView.findViewById(R.id.tv_description);
            mHighTextView = itemView.findViewById(R.id.tv_high);
            mLowTextView = itemView.findViewById(R.id.tv_low);
            mLocationTextView = itemView.findViewById(R.id.tv_location);
        }

        @Override
        public void onClick(View v) {
            mCursor.moveToPosition(getAdapterPosition());
            long date = mCursor.getLong(MainActivity.INDEX_DATE);
            mListener.onClick(date, getAdapterPosition());
        }
    }

    public void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public interface OnClickMainActivityChangeListener{
        void onClick(long date, int position);
    }
}
