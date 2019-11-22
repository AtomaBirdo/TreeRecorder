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

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView textView, textView2;
    private LocationManager locationManager;
    public static Double longitude, latitude;

    public void recordClick(View view) {
        Intent myIntent = new Intent(MainActivity.this, RecordActivity.class); //Create a new intent
        MainActivity.this.startActivity(myIntent);
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.editText);
        textView2 = findViewById(R.id.editText2);
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

    public void onClick(View view){
        //askPermission();
        textView.setText("" + latitude);
        textView2.setText("" + longitude);
    }
}