// DatabaseHelper.java
package com.example.growth_tracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TodoDB";
    private static final int DATABASE_VERSION = 3;

    // Tasks table
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_COMPLETED = "completed";
    private static final String COLUMN_AREA = "area";

    // History table
    private static final String TABLE_HISTORY = "history";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_PHYSICAL_SCORE = "physical_score";
    private static final String COLUMN_MENTAL_SCORE = "mental_score";
    private static final String COLUMN_EMOTIONAL_SCORE = "emotional_score";
    private static final String COLUMN_FINANCIAL_SCORE = "financial_score";
    private static final String COLUMN_PHYSICAL_TOTAL = "physical_total";
    private static final String COLUMN_MENTAL_TOTAL = "mental_total";
    private static final String COLUMN_EMOTIONAL_TOTAL = "emotional_total";
    private static final String COLUMN_FINANCIAL_TOTAL = "financial_total";
    private static final String COLUMN_PHYSICAL_COMPLETED = "physical_completed";
    private static final String COLUMN_MENTAL_COMPLETED = "mental_completed";
    private static final String COLUMN_EMOTIONAL_COMPLETED = "emotional_completed";
    private static final String COLUMN_FINANCIAL_COMPLETED = "financial_completed";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tasks table
        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_SCORE + " INTEGER, "
                + COLUMN_COMPLETED + " INTEGER, "
                + COLUMN_AREA + " TEXT)";
        db.execSQL(createTasksTable);

        // Create history table
        String createHistoryTable = "CREATE TABLE " + TABLE_HISTORY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DATE + " INTEGER, "
                + COLUMN_PHYSICAL_SCORE + " INTEGER, "
                + COLUMN_MENTAL_SCORE + " INTEGER, "
                + COLUMN_EMOTIONAL_SCORE + " INTEGER, "
                + COLUMN_FINANCIAL_SCORE + " INTEGER, "
                + COLUMN_PHYSICAL_TOTAL + " INTEGER, "
                + COLUMN_MENTAL_TOTAL + " INTEGER, "
                + COLUMN_EMOTIONAL_TOTAL + " INTEGER, "
                + COLUMN_FINANCIAL_TOTAL + " INTEGER, "
                + COLUMN_PHYSICAL_COMPLETED + " INTEGER, "
                + COLUMN_MENTAL_COMPLETED + " INTEGER, "
                + COLUMN_EMOTIONAL_COMPLETED + " INTEGER, "
                + COLUMN_FINANCIAL_COMPLETED + " INTEGER)";
        db.execSQL(createHistoryTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    // Task related methods
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_SCORE, task.getScore());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_AREA, task.getArea().name());
        long id = db.insert(TABLE_TASKS, null, values);
        task.setId(id);
        return id;
    }

    public ArrayList<Task> getTasksByArea(TaskArea area) {
        ArrayList<Task> tasks = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_AREA + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{area.name()});

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)),
                        TaskArea.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AREA)))
                );
                task.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1);
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_SCORE, task.getScore());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_AREA, task.getArea().name());
        db.update(TABLE_TASKS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(task.getId())});
    }

    public void deleteTask(long taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(taskId)});
    }

    public void resetAllTasksCompletion() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETED, 0);
        db.update(TABLE_TASKS, values, null, null);
    }

    // History related methods
    public void saveProgressHistory(ProgressHistory history) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DATE, history.getDate().getTime());
        values.put(COLUMN_PHYSICAL_SCORE, history.getPhysicalScore());
        values.put(COLUMN_MENTAL_SCORE, history.getMentalScore());
        values.put(COLUMN_EMOTIONAL_SCORE, history.getEmotionalScore());
        values.put(COLUMN_FINANCIAL_SCORE, history.getFinancialScore());
        values.put(COLUMN_PHYSICAL_TOTAL, history.getPhysicalTasksTotal());
        values.put(COLUMN_MENTAL_TOTAL, history.getMentalTasksTotal());
        values.put(COLUMN_EMOTIONAL_TOTAL, history.getEmotionalTasksTotal());
        values.put(COLUMN_FINANCIAL_TOTAL, history.getFinancialTasksTotal());
        values.put(COLUMN_PHYSICAL_COMPLETED, history.getPhysicalTasksCompleted());
        values.put(COLUMN_MENTAL_COMPLETED, history.getMentalTasksCompleted());
        values.put(COLUMN_EMOTIONAL_COMPLETED, history.getEmotionalTasksCompleted());
        values.put(COLUMN_FINANCIAL_COMPLETED, history.getFinancialTasksCompleted());

        db.insert(TABLE_HISTORY, null, values);
    }

    public ArrayList<ProgressHistory> getProgressHistory(int days) {
        ArrayList<ProgressHistory> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        long currentTime = System.currentTimeMillis();
        long daysInMillis = days * 24 * 60 * 60 * 1000L;
        String selection = COLUMN_DATE + " > ?";
        String[] selectionArgs = {String.valueOf(currentTime - daysInMillis)};
        String orderBy = COLUMN_DATE + " DESC";

        Cursor cursor = db.query(TABLE_HISTORY, null, selection, selectionArgs,
                null, null, orderBy);

        if (cursor.moveToFirst()) {
            do {
                ProgressHistory history = new ProgressHistory();
                history.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                history.setDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DATE))));
                history.setPhysicalScore(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PHYSICAL_SCORE)));
                history.setMentalScore(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MENTAL_SCORE)));
                history.setEmotionalScore(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EMOTIONAL_SCORE)));
                history.setFinancialScore(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FINANCIAL_SCORE)));
                history.setPhysicalTasksTotal(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PHYSICAL_TOTAL)));
                history.setMentalTasksTotal(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MENTAL_TOTAL)));
                history.setEmotionalTasksTotal(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EMOTIONAL_TOTAL)));
                history.setFinancialTasksTotal(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FINANCIAL_TOTAL)));
                history.setPhysicalTasksCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PHYSICAL_COMPLETED)));
                history.setMentalTasksCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MENTAL_COMPLETED)));
                history.setEmotionalTasksCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EMOTIONAL_COMPLETED)));
                history.setFinancialTasksCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FINANCIAL_COMPLETED)));
                historyList.add(history);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return historyList;
    }
}