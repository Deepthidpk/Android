package com.example.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/** Owns the SQLite database and exposes the few operations the app needs. */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "names.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAMES = "names";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";

    // UNIQUE + NOCASE: "Anna" and "anna" count as the same name.
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAMES + " ("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_NAME + " TEXT NOT NULL UNIQUE COLLATE NOCASE)";

    public DatabaseHelper(Context context) {
        this(context, DATABASE_NAME);
    }

    /** Lets tests use a throw-away database file. */
    public DatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAMES);
        onCreate(db);
    }

    /**
     * Inserts a name.
     *
     * @return the new row id, or -1 if the insert failed (for example a duplicate).
     */
    public long addName(String name) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        return getWritableDatabase()
                .insertWithOnConflict(TABLE_NAMES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /** Case-insensitive check used to show a friendly duplicate message. */
    public boolean nameExists(String name) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_NAMES,
                new String[]{COL_ID},
                COL_NAME + " = ? COLLATE NOCASE",
                new String[]{name},
                null, null, null, "1")) {
            return cursor.moveToFirst();
        }
    }

    /** All stored names, oldest first. */
    public List<String> getAllNames() {
        List<String> names = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_NAMES,
                new String[]{COL_NAME},
                null, null, null, null,
                COL_ID + " ASC")) {
            int nameIndex = cursor.getColumnIndexOrThrow(COL_NAME);
            while (cursor.moveToNext()) {
                names.add(cursor.getString(nameIndex));
            }
        }
        return names;
    }
}
