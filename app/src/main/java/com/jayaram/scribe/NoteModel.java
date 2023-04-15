package com.jayaram.scribe;

import android.os.Parcel;
import android.os.Parcelable;

public class NoteModel implements Parcelable {
    private int id;
    private String content;
    private String title;
    private String Date;
    private String Time;
    public NoteModel(int id, String content, String title, String date, String time) {
        this.id = id;
        this.content = content;
        this.title = title;
        Date = date;
        Time = time;
    }

    protected NoteModel(Parcel in) {
        id = in.readInt();
        content = in.readString();
        title = in.readString();
        Date = in.readString();
        Time = in.readString();
    }

    public static final Creator<NoteModel> CREATOR = new Creator<NoteModel>() {
        @Override
        public NoteModel createFromParcel(Parcel in) {
            return new NoteModel(in);
        }

        @Override
        public NoteModel[] newArray(int size) {
            return new NoteModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(content);
        parcel.writeString(title);
        parcel.writeString(Date);
        parcel.writeString(Time);
    }
}
