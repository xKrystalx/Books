package com.example.books;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.books.data.Book;
import com.example.books.data.BookInfo;
import com.example.books.data.Books;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
    implements Add.OnBookAddedListener, MyRecyclerViewAdapter.ItemClickListener, BookInfo.OnFragmentInteractionListener{

    private String filename = "data.json";
    private String data;

    public FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;
    public Add addObject;
    public BookInfo bookInfoFragment;
    public Books books;
    public String currentPhotoPath = "";

    private RecyclerView booksView;
    private MyRecyclerViewAdapter booksAdapter;
    private RecyclerView.LayoutManager bookslayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        books = new Books();
        Log.d("Bla", ""+ books);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        initializeBooksDatabase(filename);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPhotoPath = "";
                setFragment();
            }
        });

        FloatingActionButton camera = findViewById(R.id.camera_add);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                "com.example.android.fileprovider",
                                photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, 1);
                    }
                }
            }
        });

        fragmentManager = getSupportFragmentManager();
        booksView = (RecyclerView) findViewById(R.id.books_list);
        booksView.setHasFixedSize(true);
        bookslayoutManager = new LinearLayoutManager(this);
        booksView.setLayoutManager(bookslayoutManager);
        booksAdapter = new MyRecyclerViewAdapter(this, books);
        booksAdapter.setClickListener(this);
        booksView.setAdapter(booksAdapter);

        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
        }
    }

    public void initializeBooksDatabase(String filename){
        try {
            Log.println(0, "kek", "kek");
            String root = Environment.getExternalStorageDirectory() + "/" + "Books/";
            File dir = new File(root);
            if(!dir.exists()){
                dir.mkdirs();
            }
            filename =  root + filename;
            File yourFile = new File(filename);
            FileInputStream stream = new FileInputStream(yourFile);
            String jsonStr = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally {
                stream.close();
            }
            JSONObject jsonObj = new JSONObject(jsonStr);

            // Getting data JSON Array nodes
            JSONArray data  = jsonObj.getJSONArray("Books");

            // looping through All nodes
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = data.getJSONObject(i);

                String title = obj.getString("title");
                String author = obj.getString("author");
                String date = obj.getString("date_published");
                String picturePath = obj.getString("picturePath");
                if(picturePath != ""){
                    books.AddBook(new Book(title, picturePath, author, date));
                }
                else books.AddBook(new Book(title, author, date));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if(fragmentManager.getBackStackEntryCount() >1){
            fragmentManager.popBackStack();
            return;
        }
        super.onBackPressed();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode ==  1){
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
            setFragment();
        }
    }


    protected void setFragment(){
        if(fragmentManager.findFragmentById(R.id.fragment_add) != null){
            fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.fragment_add)).commitAllowingStateLoss();
        }
        addObject = new Add();
        fragmentManager.beginTransaction().replace(R.id.mainLayout, addObject).addToBackStack(null).commitAllowingStateLoss();
    }

    protected void setBookInfoFragment(){
        if(fragmentManager.findFragmentById(R.id.fragment_add) != null){
            fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.fragment_book_info)).commit();
        }
        int orientation = getResources().getConfiguration().orientation;

        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            fragmentManager.beginTransaction().replace(R.id.fragmentLayout, bookInfoFragment).commit();
        }
        else{
            fragmentManager.beginTransaction().replace(R.id.mainLayout, bookInfoFragment).addToBackStack(null).commit();
        }
    }

    @Override
    public void onBookAdded(Book book) {
        booksAdapter.notifyDataSetChanged();
        if(!currentPhotoPath.isEmpty()){
            book.picturePath = currentPhotoPath;
            currentPhotoPath = "";
        }
        books.AddBook(book);
        books.Save(filename);
        if(fragmentManager.getBackStackEntryCount() > 1){
            fragmentManager.popBackStack();
            return;
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("title", books.Books.get(position).title);
        bundle.putString("author", books.Books.get(position).author);
        bundle.putString("date", books.Books.get(position).date_published);
        bundle.putString("picturePath", books.Books.get(position).picturePath);
        bookInfoFragment = new BookInfo();
        bookInfoFragment.setArguments(bundle);
        setBookInfoFragment();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        String root = Environment.getExternalStorageDirectory() + "/" + "Books/Pictures/";
        File dir = new File(root);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                dir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
