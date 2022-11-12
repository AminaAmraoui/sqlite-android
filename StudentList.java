package com.example.sqliteapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;

public class StudentList extends AppCompatActivity {
    ListView studentlist;
    Button btndelete;
    Button btnupdate;
    List<Student> students = new ArrayList<Student>();
    StudentOpenHelper db=new StudentOpenHelper(this);
    int selectedpos;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        studentlist=(ListView) findViewById(R.id.studentList);
        btndelete=(Button)findViewById(R.id.BtnDelete);
        students =db.getAllStudents();
        ArrayList<String>lv_items=new ArrayList<String>();
        ArrayList<Integer>ids_list=new ArrayList<Integer>();
        for (Student st : students) {

            lv_items.add(st.getName() + "    " + st.getNote());
            ids_list.add(st.getID());

        }

        ArrayAdapter<String> adt=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,lv_items);
        studentlist.setAdapter(adt);
        studentlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedpos=position;
                Log.i("CheckedItem", "" + selectedpos + ""+ ids_list.get(selectedpos));
            }
        });

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder adb=new AlertDialog.Builder(StudentList.this);
                adb.setTitle("Supprimer?");
                adb.setMessage("Vous voulez vraiment supprimer l'étudiant " + ids_list.get(selectedpos) );
                adb.setNegativeButton("Annuler", null);
                adb.setPositiveButton("Supprimer", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteStudent(ids_list.get(selectedpos));
                        lv_items.remove(selectedpos);
                        ids_list.remove(selectedpos);
                        adt.notifyDataSetChanged();
                        studentlist.clearChoices();
                    }});
                adb.show();



            }


        });
        btnupdate=(Button) findViewById(R.id.BtnUpdate);
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getApplicationContext(),StudentUpdate.class);
                i.putExtra("idselected",ids_list.get(selectedpos));
                startActivity(i);
            }
        });


    }
}