package com.example.funnews;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class pinglun extends AppCompatActivity {

    private pinglun.CustomeOnClickListener listener;
    private ImageView buttomComm;
    private EditText edtComm;
    private pinglun_db dbHelper;
    private SQLiteDatabase db;
    private ArrayList<pinglun_sjk> pingluns;
    private ListView commList;
    private ListAdapter adapter;
    private ImageView buttomQuery;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pingluns=new ArrayList<pinglun_sjk>();
        adapter = new pinglun_MyAdapter(this,pingluns);
        setContentView(R.layout.comm);
        getViews();
        registerListeners();

        run();
    }

    private void getViews() {
        buttomComm =findViewById(R.id.buttomComm);
        edtComm =findViewById(R.id.edtComm);
        commList =findViewById(R.id.commList);

    }

    private void registerListeners() {
        listener =new pinglun.CustomeOnClickListener();
        buttomComm.setOnClickListener(listener);
        edtComm.setOnClickListener(listener);

    }

    private void initDBbyDatabaseHelper() {
        dbHelper =new pinglun_db(this,"pinglun",null,1);
        db =dbHelper.getWritableDatabase();
    }
    private ArrayList<pinglun_sjk> initData() {

        ArrayList<pinglun_sjk> tt=new ArrayList<>();
        Cursor cursor1 =db.rawQuery("select author_name,pinglun from pinglunss ",null);
        cursor1.moveToFirst();
        int i=0;
        while (!cursor1.isAfterLast()) {
            pinglun_sjk d=new pinglun_sjk();
            d.setAuthor_name(cursor1.getString(cursor1.getColumnIndex("author_name")));
            d.setPinglun(cursor1.getString(cursor1.getColumnIndex("pinglun")));

            i=i+1;
            Log.e("TAGxx ----------", i + cursor1.getString(cursor1.getColumnIndex("author_name")));
            pingluns.add(d);
            cursor1.moveToNext();
        }

        return pingluns;
    }
    private void intDate(){
        //遍历Cursor
        ContentValues cv =new ContentValues();
        edtComm = findViewById(R.id.edtComm);
        String querry = edtComm.getText().toString();

        db.insert("pinglunss",null,cv);
    }
    public void int1Date(){
        ContentValues cv =new ContentValues();
        edtComm = findViewById(R.id.edtComm);
        String querry = edtComm.getText().toString();
        cv.put("author_name","user");
        cv.put("pinglun",querry);

        db.insert("pinglunss",null,cv);
    }
    class CustomeOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttomComm:
                    intDate();
                    int1Date();
                    initData();
                    edtComm.setText(null);
                    break;
            }
        }
    }
    public void run(){
        initDBbyDatabaseHelper();

        commList.setAdapter(adapter);
        final ArrayList<pinglun_sjk> finalPingluns = pingluns;
        commList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        finalPingluns.get(position).getAuthor_name(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
        pingluns = initData();
    }
}
