package com.example.crud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "books_db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Book.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Book.TABLE_NAME);
        onCreate(db);
    }

    public long insertBook(String book,String author) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        // `id` will be inserted automatically.
        values.put(Book.COL_BOOK_NAME, book);
        values.put(Book.COL_AUTH,author);   // added 
        // insert row
        long id = db.insert(Book.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public Book getBook(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Book.TABLE_NAME,
                new String[]{Book.COL_ID, Book.COL_BOOK_NAME, Book.COL_AUTH},
                Book.COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare book object
        Book book = new Book(
                cursor.getInt(cursor.getColumnIndex(Book.COL_ID)),
                cursor.getString(cursor.getColumnIndex(Book.COL_BOOK_NAME)),
                cursor.getString(cursor.getColumnIndex(Book.COL_AUTH)));

        // close the db connection
        cursor.close();

        return book;
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Book.TABLE_NAME + " ORDER BY " +
                Book.COL_ID + " DESC ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Book book = new Book();
                book.setId(cursor.getInt(cursor.getColumnIndex(Book.COL_ID)));
                book.setBook(cursor.getString(cursor.getColumnIndex(Book.COL_BOOK_NAME)));
                book.setAuthor(cursor.getString(cursor.getColumnIndex(Book.COL_AUTH)));
                books.add(book);
            } while (cursor.moveToNext());
        }
        db.close();

        // return books list
        return books;
    }

    public int getBooksCount() {
        String countQuery = "SELECT  * FROM " + Book.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Book.COL_BOOK_NAME, book.getBook());
        values.put(Book.COL_AUTH,book.getAuthor());
        // updating row
        return db.update(Book.TABLE_NAME, values, Book.COL_ID + " = ?",
                new String[]{String.valueOf(book.getId())});
    }

    public void deleteBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Book.TABLE_NAME, Book.COL_ID + " = ?",
                new String[]{String.valueOf(book.getId())});
        db.close();
    }
}
