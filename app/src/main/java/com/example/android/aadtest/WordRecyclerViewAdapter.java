package com.example.android.aadtest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class WordRecyclerViewAdapter extends
        RecyclerView.Adapter<WordRecyclerViewAdapter.WordRecyclerViewHolder> {

    WordRecyclerViewAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    //private final LinkedList<String> mWordList;

    private LayoutInflater mInflater;

    // ROOMS
    private List<WordEntity> mWords;
    private static ClickListener clickListener;

    class WordRecyclerViewHolder extends RecyclerView.ViewHolder {
        public final TextView wordItemView;
        //final WordRecyclerViewAdapter mAdapter;

        public WordRecyclerViewHolder(View itemView) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.word);
            //this.mAdapter = adapter;
            //itemView.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }
    }

    public WordEntity getWordAtPosition (int position) {
        return mWords.get(position);
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        WordRecyclerViewAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }

    @NonNull
    @Override
    public WordRecyclerViewAdapter.WordRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new WordRecyclerViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(WordRecyclerViewHolder holder, int position) {
        //String mCurrent = mWordList.get(position);
        //holder.wordItemView.setText(mCurrent);
        if (mWords != null) {
            WordEntity current = mWords.get(position);
            holder.wordItemView.setText(current.getWord());
        } else {
            // Covers the case of data not being ready yet.
            holder.wordItemView.setText("No Word");
        }
    }

    @Override
    public int getItemCount() {
        //return mWordList.size();
        if (mWords != null)
            return mWords.size();
        else return 0;
    }

    // ROOMS
    void setWords(List<WordEntity> words){
        mWords = words;
        notifyDataSetChanged();
    }
}
