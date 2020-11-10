package com.bqc1990.weather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.bqc1990.weather.Database.WeatherContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, WeatherAdapter.OnClickMainActivityChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecycler;
    private WeatherAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;

    public static final String[] MAIN_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_TEMP_MAX,
            WeatherContract.WeatherEntry.COLUMN_TEMP_MIN,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    public static final int INDEX_DATE = 0;
    public static final int INDEX_MAX = 1;
    public static final int INDEX_MIN = 2;
    public static final int INDEX_WEATHER_ID = 3;
    private static final int MAIN_LOADER_ID = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecycler = (RecyclerView) findViewById(R.id.rv_list_items);
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.sfl_list_items);
        mAdapter = new WeatherAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.setHasFixedSize(true);
        mRecycler.setAdapter(mAdapter);
        Weather.initialize(this);
        getSupportLoaderManager().initLoader(MAIN_LOADER_ID, null, this);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Weather.startImmediately(MainActivity.this);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.updated), Toast.LENGTH_LONG).show();
                mSwipeRefresh.setRefreshing(false);
            }
        });

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, WeatherContract.WeatherEntry.CONTENT_URI, MAIN_PROJECTION, null, null, WeatherContract.WeatherEntry.COLUMN_DATE);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(long date, int position) {
        String dateString = String.valueOf(date);
        Uri uri = WeatherContract.WeatherEntry.CONTENT_URI.buildUpon().appendPath(dateString).build();
        Intent intent_detail = new Intent(this, DetailActivity.class);
        intent_detail.setData(uri);
        intent_detail.putExtra("cursor_position", position);
        Log.i(TAG, uri.toString());
        startActivity(intent_detail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent intent_settings = new Intent(this, SettingsActivity.class);
                startActivity(intent_settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}