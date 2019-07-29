package com.example.android.aadtest;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.List;

public class RecyclerViewActivity extends AppCompatActivity {

    //Create LinkedList for initial list of Words for RecyclerView
    //private final LinkedList<String> mWordList = new LinkedList<>();

    private RecyclerView mRecyclerView;
    //private WordRecyclerViewAdapter mAdapter;

    // ROOMS
    private WordViewModel mWordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);

        // Put initial data into the word list.
        //for (int i = 0; i < 20; i++) {
        //    mWordList.addLast("Word " + i);
        //}

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int wordListSize = mWordList.size();
                // Add a new word to the wordList.
                //mWordList.addLast("+ Word " + wordListSize);
                // Notify the adapter, that the data has changed.
                //mRecyclerView.getAdapter().notifyItemInserted(wordListSize);
                // Scroll to the bottom.
                //mRecyclerView.smoothScrollToPosition(wordListSize);

                // ROOMS
                int increment = mWordViewModel.getAllWords().getValue().size() +1;
                WordEntity word = new WordEntity("Word " + increment);
                mWordViewModel.insert(word);
            }
        });

        // Create recycler view.
        mRecyclerView = findViewById(R.id.recyclerview);
        // Create an adapter and supply the data to be displayed.
        //mAdapter = new WordRecyclerViewAdapter(this, mWordList);
        final WordRecyclerViewAdapter adapter = new WordRecyclerViewAdapter(this);
        // Connect the adapter with the recycler view.
        mRecyclerView.setAdapter(adapter);
        // Give the recycler view a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // ROOMS
        mWordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);

        mWordViewModel.getAllWords().observe(this, new Observer<List<WordEntity>>() {
            @Override
            public void onChanged(@Nullable final List<WordEntity> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setWords(words);
            }
        });

        // Add the functionality to swipe items in the
        // recycler view to delete that item
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder,
                                         int direction) {
                        int position = viewHolder.getAdapterPosition();
                        WordEntity myWord = adapter.getWordAtPosition(position);
                        // Delete the word
                        mWordViewModel.deleteWord(myWord);
                    }
                });

        helper.attachToRecyclerView(mRecyclerView);

        adapter.setOnItemClickListener(new WordRecyclerViewAdapter.ClickListener()  {

            @Override
            public void onItemClick(View v, int position) {
                WordEntity word = adapter.getWordAtPosition(position);
                String wordWord = "Clicked! " + word.getWord();
                int wordID = word.getId();

                mWordViewModel.update(new WordEntity(wordID, wordWord));
            }
        });
    }
}
