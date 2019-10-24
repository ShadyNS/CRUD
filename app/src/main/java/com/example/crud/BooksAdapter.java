package com.example.crud;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksAdapter.MyViewHolder> {

    private Context context;
    private List<Book> booksList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView book;
        public TextView author;

        public MyViewHolder(View view) {
            super(view);
            book = view.findViewById(R.id.bookName);
            author = view.findViewById(R.id.authorName);
        }
    }

    public BooksAdapter(Context context, List<Book> booksList) {
        this.context = context;
        this.booksList = booksList;
    }

    @NonNull
    @Override
    public BooksAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BooksAdapter.MyViewHolder holder, int position) {

        Book book = booksList.get(position);
        holder.book.setText(book.getBook());
        holder.author.setText(book.getAuthor());
    }

    @Override
    public int getItemCount() {
        return booksList.size();
    }
}
