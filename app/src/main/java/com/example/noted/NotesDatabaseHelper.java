package com.example.noted;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class NotesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    public NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Notes (" +
                "note_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "content TEXT NOT NULL," +
                "created_at TEXT NOT NULL)");

        db.execSQL("CREATE TABLE Categories (" +
                "category_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL UNIQUE)");

        db.execSQL("CREATE TABLE NoteCategories (" +
                "note_id INTEGER NOT NULL," +
                "category_id INTEGER NOT NULL," +
                "PRIMARY KEY (note_id, category_id)," +
                "FOREIGN KEY (note_id) REFERENCES Notes(note_id) ON DELETE CASCADE," +
                "FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS NoteCategories");
        db.execSQL("DROP TABLE IF EXISTS Categories");
        db.execSQL("DROP TABLE IF EXISTS Notes");
        onCreate(db);
    }
    public long insertNote(String title, String content, String createdAt) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Insert the note
        ContentValues noteValues = new ContentValues();
        noteValues.put("title", title);
        noteValues.put("content", content);
        noteValues.put("created_at", createdAt);
        long noteId = db.insert("Notes", null, noteValues);
        return noteId; // Return the ID of the inserted note
    }
    public long insertCategory(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues noteValues = new ContentValues();
        noteValues.put("name", title);

        try {
            return db.insertOrThrow("Categories", null, noteValues);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return -1 to indicate a failure
        }
    }

    public long insertNoteToCategory(long noteId, long categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues noteCategoryValues = new ContentValues();
        noteCategoryValues.put("note_id", noteId);
        noteCategoryValues.put("category_id", categoryId);
        return db.insert("NoteCategories", null, noteCategoryValues);
    }
    public int updateNote(long noteId, String title, String noteContent) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Prepare the values to update
        ContentValues noteValues = new ContentValues();
        noteValues.put("title", title); // Update the title
        noteValues.put("content", noteContent); // Update the content

        // Perform the update and return the number of rows affected
        return db.update(
                "Notes", // Table name
                noteValues, // Values to update
                "note_id = ?", // Where clause
                new String[]{String.valueOf(noteId)} // Arguments for the where clause
        );
    }
    public void deleteNote(long noteId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the note by its ID (ON DELETE CASCADE will handle related NoteCategories)
        db.delete("Notes", "note_id = ?", new String[]{String.valueOf(noteId)});

        // Close the database connection after the operation
        db.close();
    }

    public void deleteCategory(long categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete the category by its ID (ON DELETE CASCADE will handle related NoteCategories)
        db.delete("Categories", "category_id = ?", new String[]{String.valueOf(categoryId)});

        // Close the database connection after the operation
        db.close();
    }
    public List<NotesModel> fetchAllNotes() {
        List<NotesModel> notesModelList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT note_id, title, content, created_at FROM Notes";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);

            // Iterate through the results and add them to the list
            if (cursor.moveToFirst()) {
                do {
                    String id = String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow("note_id")));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                    String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

                    notesModelList.add(new NotesModel(id, title, content, createdAt));
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return notesModelList;
    }
    public NotesModel getNoteById(String noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        NotesModel note = null;

        String query = "SELECT note_id, title, content, created_at FROM Notes WHERE note_id = ?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{noteId});

            if (cursor != null && cursor.moveToFirst()) {
                String id = String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow("note_id")));
                String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));

                note = new NotesModel(id, title, content, createdAt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return note;
    }

}
