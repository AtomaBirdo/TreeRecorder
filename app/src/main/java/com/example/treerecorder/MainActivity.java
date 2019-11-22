package com.example.treerecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.MappedByteBuffer;
import java.util.ArrayList;
//*****************************************************
//This class is used to set up the main activity screen
//in the program and record all the trees from firebase
//to local tree class, allowing future accesses. Locat-
//ing GPS location is also performed in this class
//James Wang. Tianwei Liu, 11/21 Version 1.0
//*****************************************************
public class MainActivity extends AppCompatActivity implements LocationListener {
    //--------------------------------------------------------
    //Setting up necessary steps to link code to the activity
    //Introduced the location manager which gives GPS location
    //--------------------------------------------------------
    private TextView textView, textView2;
    private LocationManager locationManager;
    public static Double longitude, latitude;
    //------------------------------------
    //When the record button is clicked,
    //change the screen to the record page
    //------------------------------------
    public void recordClick(View view) {
        Intent myIntent = new Intent(MainActivity.this, RecordActivity.class); //Create a new intent
        MainActivity.this.startActivity(myIntent);
    }
    //----------------------------------------------------------
    //search the nearest tree according to the location entered,
    //and then pass the location of nearest tree in the array
    //to the search page
    //----------------------------------------------------------
    public void onSearch(View view){
        Intent myIntent = new Intent(this, TreeSearch.class);
        Tree reference = new Tree(Double.parseDouble(textView.getText().toString()), Double.parseDouble(textView2.getText().toString()));
        int loc = reference.getNearestTreeLoc(Tree.treeArray);
        myIntent.putExtra("location", loc);
        MainActivity.this.startActivity(myIntent);
    }

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mTreeRef = mRootRef.child("Trees");
    DatabaseReference mSingleTree;

    public static ArrayList<Tree> tempArray = new ArrayList<>();
    public static ArrayList<DatabaseReference> dr = new ArrayList<>();
    //----------------------------------------------------------------------------
    //When each time the main screen is started, the app links to the firebase and
    //reads all tree data stored in the firebase, then it would record the data
    //as a tree object with attibutes acquired from firebase, then it will store
    //the tree into a static tree array inside tree class allowing future access
    //----------------------------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("asd");
        mTreeRef.keepSynced(true);
        mTreeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                System.out.println("asdg" + dataSnapshot.toString());
                for (DataSnapshot posSnapshot: dataSnapshot.getChildren()) {
                    System.out.print(posSnapshot);
                    mSingleTree = posSnapshot.getRef();
                    mSingleTree.keepSynced(true);
                    mSingleTree.addValueEventListener(new ValueEventListener() {
                        //----------------------------------------------
                        //Create a new tree object with read attributes,
                        //then store the tree into array
                        //----------------------------------------------
                        @Override
                        public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                            Tree a = new Tree();
                            for(DataSnapshot ad: postSnapshot.getChildren()) {

                                if (ad.getKey().equalsIgnoreCase("Type")) {
                                    a.setType(ad.getValue().toString());
                                    System.out.println("ad value is" + ad.getValue().toString());
                                } else if (ad.getKey().equalsIgnoreCase("Latin")) {
                                    a.setLatin(ad.getValue().toString());
                                } else if (ad.getKey().equalsIgnoreCase("Longitude")) {
                                    a.setLongitude(Double.parseDouble(ad.getValue().toString()));
                                } else if (ad.getKey().equalsIgnoreCase("Latitude")) {
                                    a.setLatitude(Double.parseDouble(ad.getValue().toString()));
                                } else if (ad.getKey().equalsIgnoreCase("Description")) {
                                    a.setDescription(ad.getValue().toString());
                                }
                            }
                            tempArray.add(a);
                            Tree.treeArray = tempArray;
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    dr.add(mSingleTree);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        if(Tree.treeArray.size() > 0)System.out.print(Tree.treeArray.get(0).toString());
    }
    //----------------------------------------------------------
    //Setting up links to objects in the main screen at start up
    //----------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.editText);
        textView2 = findViewById(R.id.editText2);
        //---------------------------------
        //Require location access at create
        //---------------------------------
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /*if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            OnGPS();
        }else{*/
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 10);
        }else{
            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            onLocationChanged(location);
        }
        //}

        //Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        //onLocationChanged(location);

        //askPermission();
    }
    //-------------------------------------------------------
    //Methods to require and check if GPS location is granted
    //-------------------------------------------------------
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ){
        if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
            onLocationChanged(location);
        }
    }

    public void OnGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS?").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void askPermission(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
    }
    //-------------------------------------------------------------------------------------------
    //When location has changed, parse the current location into latitude and longitude variables
    //-------------------------------------------------------------------------------------------
    @Override
    public void onLocationChanged(Location location) {
        longitude = Double.parseDouble(("" + location.getLongitude()).substring(0, 8));
        latitude = Double.parseDouble(("" + location.getLatitude()).substring(0, 8));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    //--------------------------------------------------------------------------
    //When the current location button is clicked, pass the location variable to
    //the edit text view
    //--------------------------------------------------------------------------
    public void onClick(View view){
        //askPermission();
        textView.setText("" + latitude);
        textView2.setText("" + longitude);
    }
}