package com.ezbook.lamsiuwai.ezbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectionListActivity extends AppCompatActivity {
    private ListView listView ;
    private String selectPosition ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_list_activty);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();
        String value = null;
        selectPosition =null;
        if (extras != null) {
            value = extras.getString("selectionValue");
            selectPosition = extras.getString("position");
        }

        Log.d("Value",value);


        listView = (ListView) findViewById(R.id.menuSelectionList);

        String[] category = new String[] { "HKDSE", "Secondary", "Primary"};
        String[] nssBookType = new String[] { "Physics", "Chemistry", "Biology","ICT","Others"};
        String[] secondaryBookType = new String[] { "Chinese", "English", "Mathematics","Liberal Studies","Others"};
        String[] primaryBookType = new String[] { "Chinese", "English", "Mathematics","General Studies","Putonghua","Others"};
        ArrayAdapter<String> adapter ;
        switch (value){
            case "default" :adapter= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, category);break;
            case"HKDSE" :adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, nssBookType);break;
            case "Secondary":adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, secondaryBookType);break;
            case "Primary":adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, primaryBookType);break;
            default:adapter = null ;
        }


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                String  itemValue    = (String) listView.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra("ListPosition", selectPosition);
                intent.putExtra("userSelection", itemValue);
                setResult(RESULT_OK, intent);
                finish();

            }
        });
    }

}
