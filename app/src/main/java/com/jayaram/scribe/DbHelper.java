package com.jayaram.scribe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class DbHelper extends SQLiteOpenHelper {

    public static final String NOTES_TABLE = "NOTES_TABLE";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_CONTENT = "CONTENT";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_TIME = "TIME";

    public DbHelper(@Nullable Context context) {
        super(context,"Notes.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         String query= "CREATE TABLE " + NOTES_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " + COLUMN_TITLE + " TEXT , " + COLUMN_CONTENT + " TEXT , " + COLUMN_DATE + " TEXT , " + COLUMN_TIME + " TEXT )";
         db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public boolean addNote(NoteModel noteModel)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(COLUMN_TITLE,noteModel.getTitle());
        cv.put(COLUMN_CONTENT,noteModel.getContent());
        cv.put(COLUMN_DATE,noteModel.getDate());
        cv.put(COLUMN_TIME,noteModel.getTime());
        long insert = db.insert(NOTES_TABLE,null,cv);
        if(insert!=-1)
        {
            return true;
        }
        else
        {
            return false;
        }

    }


    public ArrayList<NoteModel> getAllNotes()
    {
        ArrayList<NoteModel> dataset = new ArrayList<>();
       SQLiteDatabase db= this.getReadableDatabase();
       String query = "SELECT * FROM " + NOTES_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst())
        {
            do {
               int ID= cursor.getInt(0);
               String title= cursor.getString(1);
               String content= cursor.getString(2);
               String date= cursor.getString(3);
               String time= cursor.getString(4);
               NoteModel cursorNote = new NoteModel(ID,content,title,date,time);
               dataset.add(cursorNote);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dataset;
    }

    public boolean update( NoteModel noteModel)
    {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(COLUMN_TITLE,noteModel.getTitle());
        cv.put(COLUMN_CONTENT,noteModel.getContent());
        cv.put(COLUMN_DATE,noteModel.getDate());
        cv.put(COLUMN_TIME,noteModel.getTime());
        db.update(NOTES_TABLE,cv," ID = ? " , new String[]{String.valueOf(noteModel.getId())});
        return true;
    }

    public boolean delete (NoteModel noteModel )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " DELETE FROM " + NOTES_TABLE + " WHERE " + COLUMN_ID + " = " + noteModel.getId();
        db.execSQL(query);
        db.close();
        return  true;

    }

    public boolean reOrdering(Integer u, NoteModel v )
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE , v.getTitle());
        cv.put(COLUMN_CONTENT , v.getContent());
        cv.put(COLUMN_DATE , v.getDate());
        cv.put(COLUMN_TIME , v.getTime());
        db.update(NOTES_TABLE,cv," ID = ? " , new String[]{String.valueOf(u)});
        db.close();
        return true;
    }



    public ArrayList<Integer> getPreviousList()
    {
        ArrayList<Integer> dataset = new ArrayList<>();
        SQLiteDatabase db= this.getReadableDatabase();
        String query = "SELECT * FROM " + NOTES_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst())
        {
            do {
                int ID= cursor.getInt(0);
                dataset.add(ID);

            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dataset;
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query  = " DELETE FROM " + NOTES_TABLE ;
        db.execSQL(query);
        db.close();
    }





}
