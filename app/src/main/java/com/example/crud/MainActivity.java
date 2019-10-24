package com.example.crud;

import android.content.DialogInterface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BooksAdapter mAdapter;
    private List<Book> booksList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noBooksView;

    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noBooksView = findViewById(R.id.empty_books_view);

        db = new DatabaseHelper(this);
        booksList.addAll(db.getAllBooks());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBookDialog(false, null, -1);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
        mAdapter = new BooksAdapter(this, booksList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);
        toggleEmptyBooks();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    private void createBook(String book,String author) {
        // inserting book in db and getting
        // newly inserted bok id
        long id = db.insertBook(book,author);

        // get the newly inserted book from db
        Book n = db.getBook(id);

        if (n != null) {
            // adding new book to array list at end position

            booksList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();
            toggleEmptyBooks();
        }
    }

    private void updateBook(String book, String author,  int position) {
        Book n = booksList.get(position);
        n.setBook(book);
        n.setAuthor(author);
        db.updateBook(n);
        booksList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyBooks();
    }
    private void deleteBook(int position) {
        db.deleteBook(booksList.get(position));

        // removing the book from the list
        booksList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyBooks();
    }


    private void toggleEmptyBooks() {

        if (db.getBooksCount() > 0) {
            noBooksView.setVisibility(View.GONE);
        } else {
            noBooksView.setVisibility(View.VISIBLE);
        }
    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showBookDialog(true, booksList.get(position), position);
                } else {
                    deleteBook(position);
                }
            }
        });
        builder.show();
    }


    private void showBookDialog(final boolean shouldUpdate, final Book book, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.book_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputBook = view.findViewById(R.id.editTextBookName);
        final EditText inputAuthor = view.findViewById(R.id.editTextAuthorName);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_book_title) : getString(R.string.lbl_edit_book_title));

        if (shouldUpdate && book != null) {
            inputBook.setText(book.getBook());
            inputAuthor.setText(book.getAuthor());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputBook.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter Book!", Toast.LENGTH_SHORT).show();
                    return;
                } else if(TextUtils.isEmpty(inputAuthor.getText().toString())){
                    Toast.makeText(MainActivity.this,"Enter Author", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating book
                if (shouldUpdate && book != null) {
                    // update book by it's id
                    updateBook(inputBook.getText().toString(),inputAuthor.getText().toString(), position);
                } else {
                    // create new book
                    createBook(inputBook.getText().toString(),inputAuthor.getText().toString());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
