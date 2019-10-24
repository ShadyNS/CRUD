package com.example.crud;

public class Book {
    public static final String TABLE_NAME = "books";
    public static final String COL_ID = "id";
    public static final String COL_BOOK_NAME = "Book";
    public static final String COL_AUTH = "Author";

    private int id;
    private String book;
    private String author;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_BOOK_NAME + " TEXT,"
                    + COL_AUTH + " TEXT"
                    + ")";
    public Book(){
    }
    public Book(int id, String book, String author) {
        this.id = id;
        this.book = book;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getAuthor() {
        return author;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
