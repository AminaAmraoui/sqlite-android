package com.example.sqliteapplication;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText Name;
    EditText Note;
    Button BtnAjout;
    ImageButton BtnList;
    StudentOpenHelper db=new StudentOpenHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Name=(EditText) findViewById(R.id.editName);
        Note=(EditText) findViewById(R.id.editNote);
        BtnAjout=(Button) findViewById(R.id.BtnAjout);
        BtnList=(ImageButton)findViewById(R.id.Btnlist);
        BtnAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Name.getText().toString().isEmpty()||Note.getText().toString().isEmpty()){

                    Toast.makeText(getApplicationContext(),"Renseigner tous les champs", Toast.LENGTH_SHORT).show();
                }
                else{


                    String name=Name.getText().toString();
                    Double note= Double.parseDouble(Note.getText().toString());
                    Student S= new Student(name,note);
                    db.addStudent(S);
                    Name.setText("");
                    Note.setText("");
                    Toast.makeText(getApplicationContext(),"Etudiant ajouté avec succès", Toast.LENGTH_SHORT).show();


                }

            }
        });
        BtnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getApplicationContext(),StudentList.class);
                startActivity(i);
            }
        });


    }
}