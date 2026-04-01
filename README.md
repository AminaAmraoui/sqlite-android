# SQLite CRUD — Android Example

A pedagogical Android application demonstrating full **CRUD** (Create, Read,
Update, Delete) operations using the built-in **SQLite** database, through the
`SQLiteOpenHelper` API.

The domain model used throughout this example is a simple **Student** entity,
composed of a name and a grade.

---

## 🎯 Learning Objectives

By studying this example, students will be able to:

- Design a data model class (POJO) appropriate for database persistence
- Subclass `SQLiteOpenHelper` to manage database creation and versioning
- Implement all four CRUD operations using `SQLiteDatabase` methods
- Pass data between Activities using `Intent` extras
- Understand the separation of concerns between the data layer and the UI layer

---

## 🏗️ Application Architecture

This project follows a simple **3-layer structure**:
```
┌─────────────────────────────┐
│        UI Layer             │  Activities + XML layouts
├─────────────────────────────┤
│      Data Access Layer      │  StudentOpenHelper (SQLiteOpenHelper)
├─────────────────────────────┤
│        Model Layer          │  Student (POJO)
└─────────────────────────────┘
```

---

## 📂 Project Structure
```
app/src/main/
├── java/com/example/sqliteapplication/
│   ├── Student.java              # Model: data entity
│   ├── StudentOpenHelper.java    # Data Access: database helper (CRUD)
│   ├── MainActivity.java         # UI: Add a student (Create)
│   ├── StudentList.java          # UI: Display all students (Read + Delete)
│   └── StudentUpdate.java        # UI: Edit a student (Update)
└── res/layout/
    ├── activity_main.xml
    ├── activity_student_list.xml
    └── activity_student_update.xml
```

---

## 🧩 Key Classes Explained

### 1. `Student.java` — The Model

A plain Java class (POJO) representing a student record. It encapsulates three
fields: `ID` (auto-generated), `name`, and `note` (grade).
```java
public class Student {
    private int ID;
    private String name;
    private Double note;
    // constructors, getters, setters...
}
```

> **Design note:** Separating the data model from the database logic is a
> fundamental principle of clean architecture, even in introductory projects.

---

### 2. `StudentOpenHelper.java` — The Database Helper

This is the core class of the example. It extends `SQLiteOpenHelper` and
implements all CRUD operations.

#### Database configuration
```java
private static final String DATABASE_NAME = "studentDB";
private static final int DATABASE_VERSION = 1;
private static final String TABLE_STUDENT = "student";
```

#### `onCreate()` — Table creation

Called automatically the first time the database is accessed.
```java
@Override
public void onCreate(SQLiteDatabase db) {
    String CREATE_TABLE = "CREATE TABLE " + TABLE_STUDENT + "("
        + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + NAME + " TEXT,"
        + NOTE + " REAL" + ")";
    db.execSQL(CREATE_TABLE);
}
```

#### `onUpgrade()` — Schema migration

Called when `DATABASE_VERSION` is incremented.
```java
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
    onCreate(db);
}
```

> ⚠️ **Warning for students:** Dropping the table on upgrade destroys all
> existing data. In production applications, `onUpgrade()` should apply
> incremental `ALTER TABLE` statements instead.

---

### 3. CRUD Operations — Summary Table

| Operation | Method | SQLiteDatabase API used |
|-----------|--------|------------------------|
| **Create** | `addStudent(Student s)` | `db.insert()` |
| **Read All** | `getAllStudents()` | `db.rawQuery()` + `Cursor` |
| **Read One** | `getStudent(int id)` | `db.query()` + `Cursor` |
| **Update** | `updateStudent(Student s)` | `db.update()` |
| **Delete** | `deleteStudent(int id)` | `db.delete()` |

---

#### ➕ Create — `addStudent()`
```java
public void addStudent(Student s) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(NAME, s.getName());
    values.put(NOTE, s.getNote());
    db.insert(TABLE_STUDENT, null, values);
    db.close();
}
```

`ContentValues` acts as a key-value map that the database driver serializes
into an SQL `INSERT` statement.

---

#### 📋 Read All — `getAllStudents()`
```java
Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STUDENT, null);
if (cursor.moveToFirst()) {
    do {
        Student std = new Student();
        std.setID(Integer.parseInt(cursor.getString(0)));
        std.setName(cursor.getString(1));
        std.setNote(Double.parseDouble(cursor.getString(2)));
        studentList.add(std);
    } while (cursor.moveToNext());
}
```

A `Cursor` is an iterator over the result set. Always close it after use to
avoid memory leaks.

---

#### ✏️ Update — `updateStudent()`
```java
public int updateStudent(Student std) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(NAME, std.getName());
    values.put(NOTE, std.getNote());
    return db.update(TABLE_STUDENT, values, ID + " = ?",
        new String[]{ String.valueOf(std.getID()) });
}
```

The `" = ?"` placeholder with a `String[]` argument prevents **SQL injection**,
a critical security practice even in local databases.

---

#### 🗑️ Delete — `deleteStudent()`
```java
public void deleteStudent(int id) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_STUDENT, ID + " = ?",
        new String[]{ String.valueOf(id) });
    db.close();
}
```

## 🔧 Requirements

- Android Studio (latest stable release recommended)
- Minimum SDK: API 21 (Android 5.0 Lollipop)
- Language: Java

---

## 📚 Official References

- [Save data using SQLite — Android Developers](https://developer.android.com/training/data-storage/sqlite)
- [SQLiteOpenHelper — API Reference](https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper)
- [ContentValues — API Reference](https://developer.android.com/reference/android/content/ContentValues)
- [Cursor — API Reference](https://developer.android.com/reference/android/database/Cursor)
- [SQLite Data Types](https://www.sqlite.org/datatype3.html)
- [Room Persistence Library (modern alternative)](https://developer.android.com/training/data-storage/room)

---

## 👩‍🏫 Author

Android Development Course — Pedagogical Example.
