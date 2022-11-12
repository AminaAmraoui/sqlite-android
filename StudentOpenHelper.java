package com.example.sqliteapplication;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class StudentOpenHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "studentDB";

    // Contacts table name
    private static final String TABLE_STUDENT = "student";

    // Table Columns names
    public static final String ID = "ID";
    public static final String NAME = "name";
    public static final String NOTE = "note";
    public static final String TABLE_NAME=TABLE_STUDENT;

    public StudentOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_STUDENT + "("
                + ID + " Integer PRIMARY KEY AUTOINCREMENT," + NAME + " TEXT,"
                + NOTE + " Number(2,2)" + ")";
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        // Creating tables again
        onCreate(db);
    }

    // Adding new student
    public void addStudent(Student s) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, s.getName());
        values.put(NOTE, s.getNote());

        // Inserting Row
        db.insert(TABLE_STUDENT, null, values);
        db.close(); // Closing database connection
    }
    public ArrayList<Student> getAllStudents() {
        ArrayList<Student> StudentList = new ArrayList<Student>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_STUDENT;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Student std = new Student();
                std.setID(Integer.parseInt(cursor.getString(0)));
                std.setName(cursor.getString(1));
                std.setNote(Double.parseDouble(cursor.getString(2)));
                // Adding contact to list
                StudentList.add(std);
            } while (cursor.moveToNext());
        }

        // return contact list
        return StudentList;
    }
    public void deleteStudent(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENT, ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    Student getStudent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENT, new String[] { ID,
                        NAME, NOTE }, ID + "=?",
                new String[] { String.valueOf(id) }, null,
                null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Student std = new Student(cursor.getString(1),Double.parseDouble( cursor.getString(2)));
        // return student
        return std;
    }


    public int updateStudent(Student std) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID,std.getID());
        values.put(NAME, std.getName());
        values.put(NOTE, std.getNote());

        // updating row
        return db.update(TABLE_STUDENT, values, ID + " = ?",
                new String[] { String.valueOf(std.getID()) });
    }


}
