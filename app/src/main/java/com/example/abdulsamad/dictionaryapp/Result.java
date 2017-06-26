package com.example.abdulsamad.dictionaryapp;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
public class Result extends AppCompatActivity {
    ResultAdapter adapter;
    ArrayList<ResultProvider> arrayList;
    ListView listView;
   public static ArrayList<JSONObject> object;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        listView=(ListView)findViewById(R.id.listview);
        loadData();
    }
    public void gobacktohome(View view)
    {
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }
    private void loadData()
    {
        arrayList=new ArrayList<>();
        for(int i=0;i<object.size();i++)
        {
            try
            {
                JSONObject obj=object.get(i);
                arrayList.add(new ResultProvider(obj.getString("term"), obj.getString("definition")));
            }catch (Exception ex)
            {
                Log.d("Error", "loadData: ");
            }
        }
        adapter=new ResultAdapter(this,arrayList);
        listView.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }
}