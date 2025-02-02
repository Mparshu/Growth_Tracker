package com.example.growth_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.BaseAdapter;
import java.util.ArrayList;

public class TasksAdapter  extends ArrayAdapter<Task> {
    private Context context;
    private ArrayList<Task> tasks;
    private DatabaseHelper dbHelper;

    public TasksAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0);
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
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.task_list_item, parent, false);
        }

        Task task = tasks.get(position);
        CheckBox checkBox = convertView.findViewById(R.id.taskCheckBox);
        TextView scoreText = convertView.findViewById(R.id.taskScore);

        checkBox.setText(task.getDescription());
        checkBox.setChecked(task.isCompleted());
        scoreText.setText("Score: " + task.getScore());

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            dbHelper.updateTask(task);
            notifyDataSetChanged();
        });

        return convertView;
    }

}