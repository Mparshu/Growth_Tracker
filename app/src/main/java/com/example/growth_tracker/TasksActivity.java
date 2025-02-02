package com.example.growth_tracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {
    private ListView physicalTasksList, mentalTasksList, emotionalTasksList, financialTasksList;
    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("View Tasks");

        preferences = getSharedPreferences("GrowthTrackerPreferences", MODE_PRIVATE);
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

    private void setTasksList(final TaskArea area, ListView listView) {
        ArrayList<Task> tasks = dbHelper.getTasksByArea(area);
        final TasksAdapter adapter = new TasksAdapter(this, tasks);
        listView.setAdapter(adapter);

        // Now we set a listener for the delete button inside the adapter
        adapter.setOnDeleteClickListener(new TasksAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(Task task, int position) {
                // Call the deleteTask method in the adapter to remove the task
                adapter.deleteTask(task, position);

                // Show a toast to confirm deletion
                Toast.makeText(TasksActivity.this, "Task deleted!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getScoreKey(TaskArea area) {
        // Example of how you can use the area to determine the score key
        switch (area) {
            case PHYSICAL:
                return "physical_score";
            case MENTAL:
                return "mental_score";
            case EMOTIONAL:
                return "emotional_score";
            case FINANCIAL:
                return "financial_score";
            default:
                return "default_score";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
