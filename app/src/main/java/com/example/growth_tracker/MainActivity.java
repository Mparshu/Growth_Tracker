package com.example.growth_tracker;
// MainActivity.java

import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TextView totalScoreView;
    private TextView percentageView;
    private static final int PERMISSION_REQUEST_CODE = 123;
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
    private DatabaseHelper dbHelper;
    private static final String PREF_NAME = "TodoPrefs";
    private static final String LAST_DATE_KEY = "lastDate";
    private static final String PHYSICAL_SCORE_KEY = "physicalScore";
    private static final String MENTAL_SCORE_KEY = "mentalScore";
    private static final String EMOTIONAL_SCORE_KEY = "emotionalScore";
    private static final String FINANCIAL_SCORE_KEY = "financialScore";
    private static final String USER_NAME_KEY = "userName";
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        mediaPlayer = MediaPlayer.create(this, R.raw.motivational_music);

        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false; // Update playback state
            invalidateOptionsMenu(); // Refresh menu icons
        });

        checkFirstRun();
        initializeViews();
        setupClickListeners();
        updateDateDisplay();
        checkAndResetScore();
    }

    private void checkFirstRun() {
        if (!preferences.contains(USER_NAME_KEY)) {
            showNameInputDialog();
        } else {
            updateUserName();
        }
    }

    private void updateUserName() {
        String userName = preferences.getString(USER_NAME_KEY, "User");
        TextView userNameView = findViewById(R.id.userNameView);
        userNameView.setText(userName + "'s Dashboard");
    }

    private void showNameInputDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_name_input, null);
        EditText input = dialogView.findViewById(R.id.nameInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Welcome!")
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        preferences.edit()
                                .putString(USER_NAME_KEY, name)
                                .apply();
                        updateUserName();
                    }
                });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            if (checkAndRequestPermissions()) {
                shareScreenshot();
            }
            return true;
        }
        if (item.getItemId() == R.id.task_add) {
            showAddTaskDialog();
            return true;
        }
        if (item.getItemId() == R.id.action_play) {
            // Launch Music Activity
            Intent intent = new Intent(MainActivity.this, MusicPlaybackActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    private void initializeViews() {
        // Text views for scores
        dateView = findViewById(R.id.dateView);
        totalScoreView = findViewById(R.id.totalScore);
        percentageView = findViewById(R.id.percentage);
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

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.add_new_task, null);

        EditText taskInput = view.findViewById(R.id.taskInput);
        EditText scoreInput = view.findViewById(R.id.scoreInput);
        Spinner areaSpinner = view.findViewById(R.id.areaSpinner);

        // Set up spinner
        ArrayAdapter<TaskArea> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TaskArea.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(adapter);

        builder.setView(view)
                .setTitle("Add New Task")
                .setPositiveButton("Add Task", (dialog, which) -> {
                    String description = taskInput.getText().toString().trim();
                    String scoreText = scoreInput.getText().toString().trim();
                    TaskArea area = (TaskArea) areaSpinner.getSelectedItem();

                    if (!description.isEmpty() && !scoreText.isEmpty()) {
                        try {
                            int score = Integer.parseInt(scoreText);
                            Task newTask = new Task(description, score, area);
                            dbHelper.addTask(newTask);
                            updateAllScores();
                            Toast.makeText(this, "Task added successfully",
                                    Toast.LENGTH_SHORT).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Please enter a valid score",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        dateView.setText(currentDate);
    }

    private void updateAllScores() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Get all tasks to calculate total possible score
        int totalPossibleScore = 0;
        int totalAchievedScore = 0;

        // Calculate physical scores
        ArrayList<Task> physicalTasks = dbHelper.getTasksByArea(TaskArea.PHYSICAL);
        int physicalScore = calculateAreaScore(physicalTasks);
        totalAchievedScore += physicalScore;
        totalPossibleScore += calculateAreaPossibleScore(physicalTasks);

        // Calculate mental scores
        ArrayList<Task> mentalTasks = dbHelper.getTasksByArea(TaskArea.MENTAL);
        int mentalScore = calculateAreaScore(mentalTasks);
        totalAchievedScore += mentalScore;
        totalPossibleScore += calculateAreaPossibleScore(mentalTasks);

        // Calculate emotional scores
        ArrayList<Task> emotionalTasks = dbHelper.getTasksByArea(TaskArea.EMOTIONAL);
        int emotionalScore = calculateAreaScore(emotionalTasks);
        totalAchievedScore += emotionalScore;
        totalPossibleScore += calculateAreaPossibleScore(emotionalTasks);

        // Calculate financial scores
        ArrayList<Task> financialTasks = dbHelper.getTasksByArea(TaskArea.FINANCIAL);
        int financialScore = calculateAreaScore(financialTasks);
        totalAchievedScore += financialScore;
        totalPossibleScore += calculateAreaPossibleScore(financialTasks);

        // Calculate percentage
        float percentage = 0;
        if (totalPossibleScore > 0) {
            percentage = (totalAchievedScore * 100.0f) / totalPossibleScore;
        }

        // Update views
        physicalScoreView.setText("Physical Score: " + physicalScore);
        mentalScoreView.setText("Mental Score: " + mentalScore);
        emotionalScoreView.setText("Emotional Score: " + emotionalScore);
        financialScoreView.setText("Financial Score: " + financialScore);

        // Update progress bars and task counts
        updateAreaProgress(TaskArea.PHYSICAL, dbHelper);
        updateAreaProgress(TaskArea.MENTAL, dbHelper);
        updateAreaProgress(TaskArea.EMOTIONAL, dbHelper);
        updateAreaProgress(TaskArea.FINANCIAL, dbHelper);

        // Format total score with percentage
        String totalScoreText = String.format("Total Score: %d/%d",
                totalAchievedScore, totalPossibleScore);

        String percentageText = String.format("Percentage: %.1f%%", percentage);
        percentageView.setText(percentageText);
        totalScoreView.setText(totalScoreText);
    }

    private int calculateAreaScore(ArrayList<Task> tasks) {
        int score = 0;
        for (Task task : tasks) {
            if (task.isCompleted()) {
                score += task.getScore();
            }
        }
        return score;
    }

    private int calculateAreaPossibleScore(ArrayList<Task> tasks) {
        int possibleScore = 0;
        for (Task task : tasks) {
            possibleScore += task.getScore();
        }
        return possibleScore;
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


    private void shareScreenshot() {
        View contentView = findViewById(R.id.scoresContainer);
        String userName = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .getString(USER_NAME_KEY, "User");

        // Add name to the screenshot
        TextView userNameView = findViewById(R.id.userNameView);
        userNameView.setText(userName + "'s Dashboard");

        try {
            // Ensure the view has been laid out
            contentView.post(() -> {
                try {
                    contentView.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(contentView.getDrawingCache());
                    contentView.setDrawingCacheEnabled(false);

                    // Save to cache directory (doesn't require storage permission)
                    File cachePath = new File(getCacheDir(), "images");
                    cachePath.mkdirs();
                    File imageFile = new File(cachePath, "dashboard_screenshot.png");
                    FileOutputStream stream = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                    Uri contentUri = FileProvider.getUriForFile(this,
                            getApplicationContext().getPackageName() + ".fileprovider",
                            imageFile);

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/png");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(Intent.createChooser(shareIntent, "Share Dashboard"));

                } catch (IOException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this,
                                "Error creating screenshot: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error preparing screenshot: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Log the results
            Log.d("PermissionDebug", "Number of permissions: " + grantResults.length);
            for (int i = 0; i < permissions.length; i++) {
                Log.d("PermissionDebug", "Permission: " + permissions[i] +
                        " Result: " + (grantResults[i] == PackageManager.PERMISSION_GRANTED ? "GRANTED" : "DENIED"));
            }

            // For Android 10 (API 29) and above, we don't need external storage permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                shareScreenshot();
                return;
            }

            // For older versions, check the permission
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                shareScreenshot();
            } else {
                Toast.makeText(this, "Storage permission is required to share the screenshot",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkAndRequestPermissions() {
        // For Android 10 (API 29) and above, we don't need external storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }

        // For older versions, check storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE
                );
                return false;
            }
        }
        return true;
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