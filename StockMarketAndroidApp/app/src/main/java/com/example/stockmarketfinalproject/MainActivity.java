package com.example.stockmarketfinalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
//import androidx.multidex.MultiDex;
//import androidx.multidex.MultiDex;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    //API Key: YVYF47R9IQEO0ELR
    //Finhub API KEY: c2ipsf2ad3i8gi7prq30
    FirebaseDatabase rootNode;
    DatabaseReference mDatabase;
    public static double money;
    public static int index;
    public static int parallelArrCount;
    Button searchButton;
    Button homeButton;
    Button profileButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MultiDex.install(this);
        setContentView(R.layout.activity_main);

        //Declare Widgets
        searchButton = findViewById(R.id.id_button_one);
        homeButton = findViewById(R.id.id_button_two);
        profileButton = findViewById(R.id.id_button_three);

        rootNode = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("purchases").child("0");


        //Change Image of Buttons
        searchButton.setBackgroundResource(R.drawable.searchbuttonimage);
        homeButton.setBackgroundResource(R.drawable.homebuttonimage);
        profileButton.setBackgroundResource(R.drawable.profileimagebutton);


        //Initialize Fragment
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SearchFragment blankFragment = new SearchFragment();
        fragmentTransaction.add(R.id.id_frame,blankFragment);
        fragmentTransaction.commit();

        //Change to Search Fragment
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,1.15f,1.0f,1.15f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                scaleAnimation.setDuration(100);
                searchButton.startAnimation(scaleAnimation);
                final FragmentManager fragmentManager = getSupportFragmentManager();
                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                SearchFragment blankFragment = new SearchFragment();
                fragmentTransaction.replace(R.id.id_frame,blankFragment);
                fragmentTransaction.commit();
            }
        });

        //Change to Home Fragment
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,1.15f,1.0f,1.15f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                scaleAnimation.setDuration(100);
                homeButton.startAnimation(scaleAnimation);
                final FragmentManager fragmentManager = getSupportFragmentManager();
                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                HomeFragment blankFragment = new HomeFragment();
                fragmentTransaction.replace(R.id.id_frame,blankFragment);
                fragmentTransaction.commit();

            }
        });

        //Change to Profile Fragment
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,1.15f,1.0f,1.15f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                scaleAnimation.setDuration(100);
                profileButton.startAnimation(scaleAnimation);
                final FragmentManager fragmentManager = getSupportFragmentManager();
                final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                ProfileFragment blankFragment = new ProfileFragment();
                fragmentTransaction.replace(R.id.id_frame,blankFragment);
                fragmentTransaction.commit();
            }
        });


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DecimalFormat df = new DecimalFormat("#.##");
                money = Double.parseDouble(Objects.requireNonNull(dataSnapshot.child("price").getValue()).toString());
                index = Integer.parseInt(dataSnapshot.child("index").getValue().toString());
                parallelArrCount = Integer.parseInt(dataSnapshot.child("parallel array count").getValue().toString());
                Log.d("Tag",index+"");



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

    /*
    private class downloadOnlineFile extends AsyncTask<URL,Void, JSONObject> {

        JSONObject object;
        String info;
        protected JSONObject doInBackground(URL...urls){
            try {
                URLConnection urlConnection = urls[0].openConnection();
                //URL url = new URL("api.openweathermap.org/data/2.5/find?lat="+(latitude.getText())+"&lon="+(longitude.getText())+"&cnt=3&appid=f114ed26b4552e0c921918fb372c8581");
                InputStream urlInputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlInputStream));
                info = reader.readLine();
                object = new JSONObject(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return object;
        }
        protected void onPostExecute(JSONObject object){

        }

    }
    */
}