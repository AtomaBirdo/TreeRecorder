package com.example.treerecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by James Wang on 11/7/2019
 */

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        treeType = findViewById(R.id.treeType);
        treeLatin = findViewById(R.id.treeLatin);
        treeLatitude = findViewById(R.id.treeLatitude);
        treeLongitude = findViewById(R.id.treeLongitude);
        treeDescription = findViewById(R.id.treeDescription);

        fillLocation = findViewById(R.id.fillLocation);
        submitInfo = findViewById(R.id.submit);
    }

    private TextView treeType, treeLatin, treeLatitude, treeLongitude, treeDescription;
    private Button fillLocation, submitInfo;

    public void fill(View view){ //Autofill the current longitude and latitude
        treeLatitude.setText("" + MainActivity.latitude);
        treeLongitude.setText("" + MainActivity.longitude);
    }

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    public void submit(View view){
        Tree temp = new Tree(treeType.getText().toString(), treeLatin.getText().toString(),
                Double.parseDouble(treeLatitude.getText().toString()),
                Double.parseDouble(treeLongitude.getText().toString()),
                treeDescription.getText().toString()); //Create a tree to upload to the database
        rootRef.child("Trees").child(temp.generateName()).setValue(temp); //Upload the tree to firebase
    }
}
