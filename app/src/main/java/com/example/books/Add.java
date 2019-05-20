package com.example.books;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.books.data.Book;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Add.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Add#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Add extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static String picturePath = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;

    private String filename = "data.json";
    private String data;
    public JsonArray books;
    public Gson gson;
    public List<Book> listBooks;

    private OnBookAddedListener listener;

    public Add() {
    }

    public Add newInstance(String path){
        Add fragment = new Add();
        Bundle bundle = new Bundle();
        bundle.putString("picturePath", path);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Button addBookButton;
        view = inflater.inflate(R.layout.fragment_add, container, false);
        addBookButton = view.findViewById(R.id.addbookbutton);

        final TextView title = view.findViewById(R.id.book_title);
        final TextView author = view.findViewById(R.id.book_author);
        final TextView date = view.findViewById(R.id.book_date);

        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title_str = title.getText().toString();
                String author_str = author.getText().toString();
                String date_str = date.getText().toString();
                if (listener != null && !title_str.isEmpty() && !author_str.isEmpty() && !date_str.isEmpty()) {
                    listener.onBookAdded(new Book(title_str, author_str, date_str));
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBookAddedListener) {
            listener = (OnBookAddedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnBookAddedListener {
        void onBookAdded(Book book);
    }
}
