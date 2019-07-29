package com.example.android.aadtest;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WordDao {

    @Insert
    void insert(WordEntity word);

    @Query("SELECT * from word_table ORDER BY word ASC")
    LiveData<List<WordEntity>> getAllWords();

    @Query("SELECT * from word_table LIMIT 1")
    WordEntity[] getAnyWord();

    @Query("DELETE FROM word_table")
    void deleteAll();

    @Delete
    void deleteWord(WordEntity word);

    @Update
    void update(WordEntity... word);
}
