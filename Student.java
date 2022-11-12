package com.example.sqliteapplication;

public class Student  {
    private int ID;
    private String name;
    private Double note;

    public Student() {
    }

    public Student(int  ID, String name, Double note) {
        this.ID = ID;
        this.name = name;
        this.note = note;
    }

    public Student(String name, Double note) {
        this.name = name;
        this.note = note;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public Double getNote() {
        return note;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNote(Double note) {
        this.note = note;
    }
}
