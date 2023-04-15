package com.jayaram.scribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {
   private TextView date,time;
   private EditText title;
   private EditText content;
   private NoteModel noteModel,UpdatedNote;
   private String currentDate;
   private String currentTime;
   private DbHelper dbHelper;
   private  boolean isClicked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Scribe);
        setContentView(R.layout.activity_main2);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.light_grey));
        dbHelper=new DbHelper(this);
        content=findViewById(R.id.txtContent);
        title=findViewById(R.id.edtTitle);
        date=findViewById(R.id.idDate);
        time=findViewById(R.id.idTime);
        content.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.light_grey));
        Intent i = getIntent();
        isClicked=i.getBooleanExtra("isClicked",false);
        if(isClicked) {
            UpdatedNote = i.getParcelableExtra("Clicked_Note");
            update(UpdatedNote);

        }
        else {
            currentDate = new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date());
            currentTime = new SimpleDateFormat("h:mm aa", Locale.getDefault()).format(new Date());
            date.setText(currentDate);
            time.setText(currentTime);
        }
    }

    private void update(NoteModel nm) {

             title.setText(nm.getTitle());
            content.setText(nm.getContent());
        date.setText(UpdatedNote.getDate());
        time.setText(UpdatedNote.getTime());
    }

    private  void add()
    {

        checkAdded();
        dbHelper.addNote(noteModel);
        date.setText(currentDate);
        time.setText(currentTime);
    }


   private void checkAdded()
   {
       if(title.getText().toString().equals(""))
       {
           noteModel=new NoteModel(-1,content.getText().toString(),"Untitled",currentDate,currentTime);

       }
       else
       {
           noteModel=new NoteModel(-1,content.getText().toString(),title.getText().toString(),currentDate,currentTime);
       }
   }


    @Override
    protected void onPause() {
        super.onPause();
        if(isClicked)
        {
           UpdatedNote.setContent(content.getText().toString());
           UpdatedNote.setTitle(title.getText().toString());
           dbHelper.update(UpdatedNote);
        }
        else {
            add();
        }
    }
}