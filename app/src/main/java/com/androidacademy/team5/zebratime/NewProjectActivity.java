package com.androidacademy.team5.zebratime;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidacademy.team5.zebratime.entity.Project;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);
        TextInputLayout til = findViewById(R.id.text_input_ed_text);
        EditText etl = findViewById(R.id.ed_text);
        til.setHint(getString(R.string.enter_title));
        Button okBtn = (Button) findViewById(R.id.ok_button);

        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Context context = getApplicationContext();
                CharSequence text = "Empty title";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);

                EditText edTitle = (EditText) findViewById(R.id.ed_text);
                final String titleNewProject = edTitle.getText().toString();
                if(titleNewProject.matches("")){
                    toast.show();
                }
                else
                {
                    FirebaseDatabase database =  FirebaseDatabase.getInstance();
                    DatabaseReference mRef =  database.getReference().child("Projects");

                    Project newProject = new Project(titleNewProject);

                    newProject.setId(mRef.push().getKey());
                    mRef.child(newProject.getId()).setValue(newProject);
                    finish();
                }
            }
        });
    }
}
