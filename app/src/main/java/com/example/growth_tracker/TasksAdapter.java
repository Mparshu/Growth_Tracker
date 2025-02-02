package com.example.growth_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class TasksAdapter extends ArrayAdapter<Task> {
    private Context context;
    private ArrayList<Task> tasks;
    private DatabaseHelper dbHelper;

    // Listener to handle delete button click
    public interface OnDeleteClickListener {
        void onDeleteClick(Task task, int position);
    }

    private OnDeleteClickListener onDeleteClickListener;

    public TasksAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
        this.context = context;
        this.tasks = tasks;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Task getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_list_item, parent, false);
        }

        Task task = tasks.get(position);

        CheckBox checkBox = convertView.findViewById(R.id.taskCheckBox);
        TextView scoreText = convertView.findViewById(R.id.taskScore);
        Button deleteButton = convertView.findViewById(R.id.deleteTaskButton);

        // Set task description and score
        checkBox.setText(task.getDescription());
        checkBox.setChecked(task.isCompleted());
        scoreText.setText("Score: " + task.getScore());

        // Handle checkbox change
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            dbHelper.updateTask(task);
            notifyDataSetChanged();
        });

        // Handle delete button click
        deleteButton.setOnClickListener(v -> {
            deleteTask(task, position);
        });

        return convertView;
    }

    // Setter for the OnDeleteClickListener
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    // Method to delete task from both list and database
    public void deleteTask(Task task, int position) {
        tasks.remove(position);  // Remove task from the list
        dbHelper.deleteTask(task.getId());  // Delete the task from the database
        notifyDataSetChanged();  // Notify adapter to refresh the list
        Toast.makeText(context, "Task deleted!", Toast.LENGTH_SHORT).show();  // Show a confirmation
    }
}
