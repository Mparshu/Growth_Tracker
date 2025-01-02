package com.example.growth_tracker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    private ListView historyList;
    private DatabaseHelper dbHelper;
    private static final String TAG = "HistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Progress History");

        dbHelper = new DatabaseHelper(this);
        historyList = findViewById(R.id.historyList);

        loadHistory();
    }

    private void loadHistory() {
        try {
            ArrayList<ProgressHistory> historyData = dbHelper.getProgressHistory(30); // get last 30 days
            Log.d(TAG, "Retrieved history items: " + historyData.size());

            if (historyData.isEmpty()) {
                Toast.makeText(this, "No history data available yet", Toast.LENGTH_LONG).show();
                return;
            }

            // Display first entry details in log for debugging
            if (historyData.size() > 0) {
                ProgressHistory firstItem = historyData.get(0);
                Log.d(TAG, "First history item - Date: " + firstItem.getDate() +
                        ", Total Score: " + firstItem.getTotalScore());
            }

            CustomHistoryAdapter adapter = new CustomHistoryAdapter(this, historyData);
            historyList.setAdapter(adapter);

        } catch (Exception e) {
            Log.e(TAG, "Error loading history: " + e.getMessage());
            Toast.makeText(this, "Error loading history data", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private class CustomHistoryAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<ProgressHistory> historyData;
        private SimpleDateFormat dateFormat;

        public CustomHistoryAdapter(Context context, ArrayList<ProgressHistory> historyData) {
            this.context = context;
            this.historyData = historyData;
            this.dateFormat = new SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault());
        }

        @Override
        public int getCount() {
            return historyData.size();
        }

        @Override
        public Object getItem(int position) {
            return historyData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.history_item, parent, false);
            }

            try {
                ProgressHistory item = historyData.get(position);

                TextView dateView = convertView.findViewById(R.id.historyDate);
                TextView scoreView = convertView.findViewById(R.id.historyTotalScore);

                dateView.setText(dateFormat.format(item.getDate()));
                scoreView.setText("Total Score: " + item.getTotalScore());

                Log.d(TAG, "Setting up view for date: " + dateFormat.format(item.getDate()));

            } catch (Exception e) {
                Log.e(TAG, "Error in getView: " + e.getMessage());
                e.printStackTrace();
            }

            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}