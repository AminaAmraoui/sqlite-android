package com.example.sqliteapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StudentUpdate extends AppCompatActivity {

    EditText nameUpdate;
    EditText noteUpdate;
    Button update;
    StudentOpenHelper db=new StudentOpenHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_update);
        nameUpdate=(EditText) findViewById(R.id.updateName);
        noteUpdate=(EditText) findViewById(R.id.UpdateNote);
        update=(Button) findViewById(R.id.save);
        int selectedpos=getIntent().getIntExtra("idselected",-1);
        Log.i("CheckedItem", "" + selectedpos + "");
        Student std=db.getStudent(selectedpos);
        nameUpdate.setText(std.getName());
        noteUpdate.setText(std.getNote().toString());
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Student std_update=new Student(selectedpos,nameUpdate.getText().toString(),Double.parseDouble(noteUpdate.getText().toString()));
                int updatedone=db.updateStudent(std_update);
                Log.i("Updating", "" + updatedone + "");
                Intent i= new Intent(getApplicationContext(),StudentList.class);
                startActivity(i);
            }
        });
    }
}