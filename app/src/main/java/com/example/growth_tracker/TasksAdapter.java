package com.example.growth_tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;
import java.util.ArrayList;

public class TasksAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Task> tasks;

    public TasksAdapter(Context context, ArrayList<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
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
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        Task task = tasks.get(position);
        textView.setText(task.toString());

        if (task.isCompleted()) {
            textView.setBackgroundColor(0x1F00FF00); // Light green background
        } else {
            textView.setBackgroundColor(0x00000000); // Transparent background
        }

        return convertView;
    }

    public void updateData(ArrayList<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }
}