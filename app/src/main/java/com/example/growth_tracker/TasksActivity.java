package com.example.growth_tracker;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {
    private ListView physicalTasksList, mentalTasksList, emotionalTasksList, financialTasksList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View Tasks");

        initializeViews();
        loadTasks();
    }

    private void initializeViews() {
        physicalTasksList = findViewById(R.id.physicalTasksList);
        mentalTasksList = findViewById(R.id.mentalTasksList);
        emotionalTasksList = findViewById(R.id.emotionalTasksList);
        financialTasksList = findViewById(R.id.financialTasksList);
    }

    private void loadTasks() {
        dbHelper = new DatabaseHelper(this);

        setTasksList(TaskArea.PHYSICAL, physicalTasksList);
        setTasksList(TaskArea.MENTAL, mentalTasksList);
        setTasksList(TaskArea.EMOTIONAL, emotionalTasksList);
        setTasksList(TaskArea.FINANCIAL, financialTasksList);
    }

    private void setTasksList(TaskArea area, ListView listView) {
        ArrayList<Task> tasks = dbHelper.getTasksByArea(area);
        TasksAdapter adapter = new TasksAdapter(this, tasks);
        listView.setAdapter(adapter);
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