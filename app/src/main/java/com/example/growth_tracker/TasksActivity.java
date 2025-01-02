package com.example.growth_tracker;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class TasksActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "TodoPrefs";
    private static final String PHYSICAL_SCORE_KEY = "physicalScore";
    private static final String MENTAL_SCORE_KEY = "mentalScore";
    private static final String EMOTIONAL_SCORE_KEY = "emotionalScore";
    private static final String FINANCIAL_SCORE_KEY = "financialScore";

    private EditText taskInput;
    private EditText scoreInput;
    private Spinner areaSpinner;
    private Map<TaskArea, ListView> taskListViews;
    private Map<TaskArea, ArrayAdapter<Task>> adapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Tasks");

        dbHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        initializeViews();
        setupAreaSpinner();
        setupTaskLists();
        loadTasks();
    }

    private void initializeViews() {
        taskInput = findViewById(R.id.taskInput);
        scoreInput = findViewById(R.id.scoreInput);
        areaSpinner = findViewById(R.id.areaSpinner);
        Button addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(v -> addTask());

        taskListViews = new EnumMap<>(TaskArea.class);
        adapters = new EnumMap<>(TaskArea.class);

        // Initialize ListView for each area
        taskListViews.put(TaskArea.PHYSICAL, findViewById(R.id.physicalTasksList));
        taskListViews.put(TaskArea.MENTAL, findViewById(R.id.mentalTasksList));
        taskListViews.put(TaskArea.EMOTIONAL, findViewById(R.id.emotionalTasksList));
        taskListViews.put(TaskArea.FINANCIAL, findViewById(R.id.financialTasksList));

        // Set headers visibility
        for (TaskArea area : TaskArea.values()) {
            TextView header = findViewById(getHeaderId(area));
            if (header != null) {
                header.setText(area.getDisplayName());
            }
        }
    }

    private int getHeaderId(TaskArea area) {
        switch (area) {
            case PHYSICAL: return R.id.physicalHeader;
            case MENTAL: return R.id.mentalHeader;
            case EMOTIONAL: return R.id.emotionalHeader;
            case FINANCIAL: return R.id.financialHeader;
            default: return 0;
        }
    }

    private String getScoreKey(TaskArea area) {
        switch (area) {
            case PHYSICAL: return PHYSICAL_SCORE_KEY;
            case MENTAL: return MENTAL_SCORE_KEY;
            case EMOTIONAL: return EMOTIONAL_SCORE_KEY;
            case FINANCIAL: return FINANCIAL_SCORE_KEY;
            default: return "";
        }
    }

    private void setupAreaSpinner() {
        ArrayAdapter<TaskArea> areaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TaskArea.values());
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(areaAdapter);
    }

    private void setupTaskLists() {
        for (TaskArea area : TaskArea.values()) {
            ArrayList<Task> tasks = new ArrayList<>();
            ArrayAdapter<Task> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, tasks);
            adapters.put(area, adapter);
            ListView listView = taskListViews.get(area);
            listView.setAdapter(adapter);

            setupListViewListeners(listView, area);
        }
    }

    private void setupListViewListeners(ListView listView, final TaskArea area) {
        // Click to complete task
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Task task = adapters.get(area).getItem(position);
            if (task != null && !task.isCompleted()) {
                task.setCompleted(true);
                dbHelper.updateTask(task);

                // Update area-specific score
                String scoreKey = getScoreKey(task.getArea());
                int currentScore = preferences.getInt(scoreKey, 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(scoreKey, currentScore + task.getScore());
                editor.apply();

                adapters.get(area).notifyDataSetChanged();
                Toast.makeText(TasksActivity.this,
                        "Task completed! Earned " + task.getScore() + " points!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Long click to delete task
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Task task = adapters.get(area).getItem(position);
            if (task != null) {
                if (task.isCompleted()) {
                    // Subtract score if task was completed
                    String scoreKey = getScoreKey(task.getArea());
                    int currentScore = preferences.getInt(scoreKey, 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(scoreKey, currentScore - task.getScore());
                    editor.apply();
                }
                dbHelper.deleteTask(task.getId());
                adapters.get(area).remove(task);
                Toast.makeText(TasksActivity.this,
                        "Task deleted!", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void loadTasks() {
        for (TaskArea area : TaskArea.values()) {
            ArrayList<Task> tasks = dbHelper.getTasksByArea(area);
            ArrayAdapter<Task> adapter = adapters.get(area);
            adapter.clear();
            adapter.addAll(tasks);
        }
    }

    private void addTask() {
        String description = taskInput.getText().toString().trim();
        String scoreText = scoreInput.getText().toString().trim();

        if (!description.isEmpty() && !scoreText.isEmpty()) {
            try {
                int score = Integer.parseInt(scoreText);
                TaskArea area = (TaskArea) areaSpinner.getSelectedItem();
                Task newTask = new Task(description, score, area);

                dbHelper.addTask(newTask);
                adapters.get(area).add(newTask);

                taskInput.setText("");
                scoreInput.setText("");

                Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this,
                        "Please enter a valid score", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this,
                    "Please fill in all fields", Toast.LENGTH_SHORT).show();
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