package com.example.stockmarketfinalproject;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    String [] Companies = new String[MainActivity.parallelArrCount];
    double [] Prices = new double[MainActivity.parallelArrCount];
    AnyChartView anyChartView;
    double cv;
    double portfolioValue;
    int tempParallelArrCount;
    TextView portfolioValueDisplay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);
        rootNode = FirebaseDatabase.getInstance();
        portfolioValue = 0.0;
        reference = FirebaseDatabase.getInstance().getReference().child("purchases");
        portfolioValueDisplay = v.findViewById(R.id.id_portfolioValue);
        anyChartView = v.findViewById(R.id.any_chart_view);

        tempParallelArrCount = MainActivity.parallelArrCount;
        reference.child("0").child("parallel array count").setValue(tempParallelArrCount-1);
        reference.child("0").child("parallel array count").setValue(tempParallelArrCount);


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i = 0; i<tempParallelArrCount; i++){
                    Companies[i] = Objects.requireNonNull(snapshot.child(0 + "").child("stocks").child(i + "").getValue()).toString();
                    cv = (Double.parseDouble((Objects.requireNonNull(snapshot.child(0 + "").child("current value").child(i + "").getValue())).toString()));
                    Prices[i] = (Double.parseDouble(Objects.requireNonNull(snapshot.child(0 + "").child("amount").child(i + "").getValue()).toString())*cv);
                    portfolioValue+=Prices[i];
                }
                DecimalFormat df = new DecimalFormat("#.##");
                portfolioValueDisplay.setText("Investing: $"+df.format(portfolioValue+MainActivity.money));
                portfolioValueDisplay.setTypeface(portfolioValueDisplay.getTypeface(), Typeface.BOLD);
                setupPieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i = 0; i<MainActivity.parallelArrCount; i++) {

                    Companies[i] = Objects.requireNonNull(snapshot.child(0 + "").child("stocks").child(i + "").getValue()).toString();
                    try {

                        URL url = new URL("https://finnhub.io/api/v1/quote?symbol="+Companies[i]+"&token=c2ipsf2ad3i8gi7prq30");
                        downloadOnlineFile onlineFile = new downloadOnlineFile();
                        MyTaskParams myTaskParams = new MyTaskParams(i, url);
                        onlineFile.execute(myTaskParams);
                        //Prices[i] = (Double.parseDouble(Objects.requireNonNull(snapshot.child(0 + "").child("amount").child(i + "").getValue()).toString()));


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        return v;
    }
    public void setupPieChart(){
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();
        for(int i = 0; i < Companies.length; i++){
            if(Prices[i] != 0){
                dataEntries.add(new ValueDataEntry(Companies[i],Prices[i]));
            }
        }

        pie.data(dataEntries);
        pie.labels().position("outside");
        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);
        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Portfolio")
                .padding(0d, 0d, 10d, 0d);
        anyChartView.setChart(pie);
    }
    class downloadOnlineFile extends AsyncTask<MyTaskParams,Void, JSONObject> {

        JSONObject object;
        String info;



        protected JSONObject doInBackground(MyTaskParams ... myTaskParams) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) myTaskParams[0].url.openConnection();
                InputStream urlInputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlInputStream));
                info = reader.readLine();
                object = new JSONObject(info);
                cv = Double.parseDouble(object.getString("c"));
                Log.d("Tag", "cv: "+cv);
                Log.d("Tag", "MyTaskParams: "+myTaskParams[0].foo);
                rootNode = FirebaseDatabase.getInstance();
                reference = FirebaseDatabase.getInstance().getReference().child("purchases");
                reference.child("0").child("current value").child(myTaskParams[0].foo+"").setValue(cv);
                return object;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }




        protected void onPostExecute(JSONObject object) {
            //try {

              //  cv = Double.parseDouble(object.getString("c"));
                //CurrentValues[temp] = cv;



//            } catch (JSONException e) {
  //              e.printStackTrace();
    //        }
        }

    }
    private static class MyTaskParams {
        int foo;
        URL url;

        MyTaskParams(int foo, URL url) {
            this.foo = foo;
            this.url = url;
        }
    }

}