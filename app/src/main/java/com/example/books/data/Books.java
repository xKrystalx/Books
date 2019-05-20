package com.example.books.data;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Books {
    public List<Book> Books;

    public Books() {
        this.Books = new ArrayList<Book>();
    }

    public Books(ArrayList<Book> list) {
        this.Books = list;
    }

    public void AddBook(Book book){
        if(book != null){
            Books.add(book);
        }
    }

    public void Save(String filename){
        Gson gson = new Gson();
        String json = gson.toJson(this);
        try {
            String root = Environment.getExternalStorageDirectory() + "/" + "Books/";
            File dir = new File(root);
            if(!dir.exists()){
                dir.mkdirs();
            }
            filename =  root + filename;
            File yourFile = new File(filename);
            FileOutputStream output = new FileOutputStream(yourFile);
            byte b[] = json.getBytes();
            output.write(b);
            output.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
