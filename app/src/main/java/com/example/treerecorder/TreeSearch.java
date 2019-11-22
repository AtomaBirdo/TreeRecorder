package com.example.treerecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
//**********************************************************************************
//This class is designed to read the location of the nearest tree and show it on the
//Tree search screen.
//Tianwei Liu, 11/21, Version 1.0
//**********************************************************************************
public class TreeSearch extends AppCompatActivity {
    TextView textLatitude;
    TextView textLongitude;
    TextView textName;
    TextView textLatin;
    TextView textDescription;

    //-----------------------------------------------------------
    //Obtain the location of nearest tree and display accordingly
    //-----------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        Intent myIntent = getIntent();
        int loc = myIntent.getIntExtra("location",0);
        Tree a = Tree.treeArray.get(loc);
        textLatitude.setText("" + a.getLatitude());
        textLongitude.setText("" + a.getLongitude());
        textName.setText(a.getType());
        textLatin.setText(a.getLatin());
        textDescription.setText(a.getDescription());

    }
    //----------------------------------------------------------------------
    //When the activity is created, link all objects to the object on screen
    //----------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_search);
        textLatitude = (TextView)findViewById(R.id.textLatitude);
        textLongitude = (TextView)findViewById(R.id.textLongitude);
        textName = (TextView)findViewById(R.id.textName);
        textLatin = (TextView)findViewById(R.id.textLatin);
        textDescription = (TextView)findViewById(R.id.textDescription);
    }
}
