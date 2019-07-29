package com.example.android.aadtest;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "word_table")
public class WordEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "word")
    private String mWord;

    public WordEntity(@NonNull String word) {
        this.mWord = word;
    }

    public String getWord(){return this.mWord;}

    /*
     * This constructor is annotated using @Ignore, because Room expects only
     * one constructor by default in an entity class.
     */

    @Ignore
    public WordEntity(int id, @NonNull String word) {
        this.id = id;
        this.mWord = word;
    }

    public int getId() {return id;}

    public void setId(int id) {
        this.id = id;
    }
}
