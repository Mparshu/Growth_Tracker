package com.example.growth_tracker;
// MainActivity.java

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView totalScoreView;
    private TextView dateView;
    private TextView physicalScoreView;
    private TextView mentalScoreView;
    private TextView emotionalScoreView;
    private TextView financialScoreView;
    private Button historyButton;
    private Button tasksButton;
    private ProgressBar physicalProgress;
    private ProgressBar mentalProgress;
    private ProgressBar emotionalProgress;
    private ProgressBar financialProgress;
    private TextView physicalTasksCount;
    private TextView mentalTasksCount;
    private TextView emotionalTasksCount;
    private TextView financialTasksCount;

    private SharedPreferences preferences;
    private static final String PREF_NAME = "TodoPrefs";
    private static final String LAST_DATE_KEY = "lastDate";
    private static final String PHYSICAL_SCORE_KEY = "physicalScore";
    private static final String MENTAL_SCORE_KEY = "mentalScore";
    private static final String EMOTIONAL_SCORE_KEY = "emotionalScore";
    private static final String FINANCIAL_SCORE_KEY = "financialScore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        initializeViews();
        setupClickListeners();
        updateDateDisplay();
        checkAndResetScore();
    }

    private void initializeViews() {
        // Text views for scores
        dateView = findViewById(R.id.dateView);
        totalScoreView = findViewById(R.id.totalScore);
        physicalScoreView = findViewById(R.id.physicalScore);
        mentalScoreView = findViewById(R.id.mentalScore);
        emotionalScoreView = findViewById(R.id.emotionalScore);
        financialScoreView = findViewById(R.id.financialScore);

        // Progress bars
        physicalProgress = findViewById(R.id.physicalProgress);
        mentalProgress = findViewById(R.id.mentalProgress);
        emotionalProgress = findViewById(R.id.emotionalProgress);
        financialProgress = findViewById(R.id.financialProgress);

        // Task count views
        physicalTasksCount = findViewById(R.id.physicalTasksCount);
        mentalTasksCount = findViewById(R.id.mentalTasksCount);
        emotionalTasksCount = findViewById(R.id.emotionalTasksCount);
        financialTasksCount = findViewById(R.id.financialTasksCount);

        // Buttons
        tasksButton = findViewById(R.id.tasksButton);
        historyButton = findViewById(R.id.historyButton);
    }

    private void setupClickListeners() {
        tasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TasksActivity.class);
                startActivity(intent);
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAndResetScore();
        updateAllScores();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        dateView.setText(currentDate);
    }

    private void updateAllScores() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        int physicalScore = preferences.getInt(PHYSICAL_SCORE_KEY, 0);
        int mentalScore = preferences.getInt(MENTAL_SCORE_KEY, 0);
        int emotionalScore = preferences.getInt(EMOTIONAL_SCORE_KEY, 0);
        int financialScore = preferences.getInt(FINANCIAL_SCORE_KEY, 0);
        int totalScore = physicalScore + mentalScore + emotionalScore + financialScore;

        // Update scores
        physicalScoreView.setText("Physical Score: " + physicalScore);
        mentalScoreView.setText("Mental Score: " + mentalScore);
        emotionalScoreView.setText("Emotional Score: " + emotionalScore);
        financialScoreView.setText("Financial Score: " + financialScore);
        totalScoreView.setText("Total Score: " + totalScore);

        // Update progress bars and task counts
        updateAreaProgress(TaskArea.PHYSICAL, dbHelper);
        updateAreaProgress(TaskArea.MENTAL, dbHelper);
        updateAreaProgress(TaskArea.EMOTIONAL, dbHelper);
        updateAreaProgress(TaskArea.FINANCIAL, dbHelper);
    }

    private void updateAreaProgress(TaskArea area, DatabaseHelper dbHelper) {
        ArrayList<Task> tasks = dbHelper.getTasksByArea(area);
        int totalTasks = tasks.size();
        int completedTasks = 0;
        for (Task task : tasks) {
            if (task.isCompleted()) {
                completedTasks++;
            }
        }

        // Calculate progress percentage
        int progress = totalTasks > 0 ? (completedTasks * 100) / totalTasks : 0;

        // Update UI based on area
        switch (area) {
            case PHYSICAL:
                physicalProgress.setProgress(progress);
                physicalTasksCount.setText("Completed: " + completedTasks + "/" + totalTasks);
                break;
            case MENTAL:
                mentalProgress.setProgress(progress);
                mentalTasksCount.setText("Completed: " + completedTasks + "/" + totalTasks);
                break;
            case EMOTIONAL:
                emotionalProgress.setProgress(progress);
                emotionalTasksCount.setText("Completed: " + completedTasks + "/" + totalTasks);
                break;
            case FINANCIAL:
                financialProgress.setProgress(progress);
                financialTasksCount.setText("Completed: " + completedTasks + "/" + totalTasks);
                break;
        }
    }

    private void checkAndResetScore() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String lastDate = preferences.getString(LAST_DATE_KEY, "");

        if (!currentDate.equals(lastDate)) {
            // Save current progress to history before reset
            saveProgressHistory();

            // Reset all scores
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(PHYSICAL_SCORE_KEY, 0);
            editor.putInt(MENTAL_SCORE_KEY, 0);
            editor.putInt(EMOTIONAL_SCORE_KEY, 0);
            editor.putInt(FINANCIAL_SCORE_KEY, 0);
            editor.putString(LAST_DATE_KEY, currentDate);
            editor.apply();

            // Reset task completion status
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.resetAllTasksCompletion();
        }
    }

    private void saveProgressHistory() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        ProgressHistory history = new ProgressHistory();

        // Set current date explicitly
        history.setDate(new Date());

        // Get current scores
        int physicalScore = preferences.getInt(PHYSICAL_SCORE_KEY, 0);
        int mentalScore = preferences.getInt(MENTAL_SCORE_KEY, 0);
        int emotionalScore = preferences.getInt(EMOTIONAL_SCORE_KEY, 0);
        int financialScore = preferences.getInt(FINANCIAL_SCORE_KEY, 0);

        // Set scores
        history.setPhysicalScore(physicalScore);
        history.setMentalScore(mentalScore);
        history.setEmotionalScore(emotionalScore);
        history.setFinancialScore(financialScore);

        // Get and set task counts for each area
        ArrayList<Task> physicalTasks = dbHelper.getTasksByArea(TaskArea.PHYSICAL);
        ArrayList<Task> mentalTasks = dbHelper.getTasksByArea(TaskArea.MENTAL);
        ArrayList<Task> emotionalTasks = dbHelper.getTasksByArea(TaskArea.EMOTIONAL);
        ArrayList<Task> financialTasks = dbHelper.getTasksByArea(TaskArea.FINANCIAL);

        // Physical tasks
        history.setPhysicalTasksTotal(physicalTasks.size());
        int physicalCompleted = 0;
        for (Task task : physicalTasks) {
            if (task.isCompleted()) {
                physicalCompleted++;
            }
        }
        history.setPhysicalTasksCompleted(physicalCompleted);

        // Mental tasks
        history.setMentalTasksTotal(mentalTasks.size());
        int mentalCompleted = 0;
        for (Task task : mentalTasks) {
            if (task.isCompleted()) {
                mentalCompleted++;
            }
        }
        history.setMentalTasksCompleted(mentalCompleted);

        // Emotional tasks
        history.setEmotionalTasksTotal(emotionalTasks.size());
        int emotionalCompleted = 0;
        for (Task task : emotionalTasks) {
            if (task.isCompleted()) {
                emotionalCompleted++;
            }
        }
        history.setEmotionalTasksCompleted(emotionalCompleted);

        // Financial tasks
        history.setFinancialTasksTotal(financialTasks.size());
        int financialCompleted = 0;
        for (Task task : financialTasks) {
            if (task.isCompleted()) {
                financialCompleted++;
            }
        }
        history.setFinancialTasksCompleted(financialCompleted);

        // Save to database
        dbHelper.saveProgressHistory(history);

        // Add log to verify data is being saved
        Toast.makeText(this, "Progress saved for " + new SimpleDateFormat("MMM d, yyyy",
                Locale.getDefault()).format(history.getDate()), Toast.LENGTH_SHORT).show();
    }


}