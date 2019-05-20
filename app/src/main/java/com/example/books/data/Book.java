package com.example.books.data;

import com.example.books.R;

public class Book {
    public String title;
    public String picturePath;
    public String author;
    public String date_published;

    public Book(String title, String author, String date_published) {
        this.title = title;
        this.picturePath = "";
        this.author = author;
        this.date_published = date_published;
    }

    public Book(String title, String picturePath, String author, String date_published) {
        this.title = title;
        this.picturePath = picturePath;
        this.author = author;
        this.date_published = date_published;
    }

    public String getTitle() {
        return title;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate_published() {
        return date_published;
    }
}
