package com.example.android.aadtest;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class WordRepository {

    private WordDao mWordDao;
    private LiveData<List<WordEntity>> mAllWords;

    WordRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mWordDao = db.wordDao();
        mAllWords = mWordDao.getAllWords();
    }

    LiveData<List<WordEntity>> getAllWords() {
        return mAllWords;
    }

    public void insert (WordEntity word) {
        new insertAsyncTask(mWordDao).execute(word);
    }

    public void deleteAll()  {
        new deleteAllWordsAsyncTask(mWordDao).execute();
    }

    public void deleteWord(WordEntity word)  {
        new deleteWordAsyncTask(mWordDao).execute(word);
    }

    public void update(WordEntity word)  {
        new updateWordAsyncTask(mWordDao).execute(word);
    }

    private static class insertAsyncTask extends AsyncTask<WordEntity, Void, Void> {

        private WordDao mAsyncTaskDao;

        insertAsyncTask(WordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final WordEntity... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAllWordsAsyncTask extends AsyncTask<Void, Void, Void> {
        private WordDao mAsyncTaskDao;

        deleteAllWordsAsyncTask(WordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    private static class deleteWordAsyncTask extends AsyncTask<WordEntity, Void, Void> {
        private WordDao mAsyncTaskDao;

        deleteWordAsyncTask(WordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final WordEntity... params) {
            mAsyncTaskDao.deleteWord(params[0]);
            return null;
        }
    }

    /**
     *  Updates a word in the database.
     */
    private static class updateWordAsyncTask extends AsyncTask<WordEntity, Void, Void> {
        private WordDao mAsyncTaskDao;

        updateWordAsyncTask(WordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final WordEntity... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }
}
