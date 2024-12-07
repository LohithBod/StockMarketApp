package com.example.stockmarketfinalproject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.renderscript.Sampler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    FirebaseDatabase rootNode;
    DatabaseReference mDatabase;
    DatabaseReference reference;
    ArrayList<Purchase> purchaseList;
    ListView listView;
    TextView currentMoney;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        purchaseList = new ArrayList<>();
        listView = v.findViewById(R.id.id_listView);
        currentMoney = v.findViewById(R.id.id_balance);
        rootNode = FirebaseDatabase.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("purchases").child("0");
        DecimalFormat df = new DecimalFormat("#.##");
        currentMoney.setText("Account Balance:  $"+df.format(MainActivity.money));
        final ArrayList<String> list = new ArrayList<>();


        final CustomAdapter customAdapter;
        customAdapter = new CustomAdapter(getActivity().getBaseContext(), R.layout.adapter_custom, purchaseList);
        Log.d("Tag",purchaseList.toString());
        listView.setAdapter(customAdapter);
        Log.d("Tag","2");

        for(int i = 1; i <= MainActivity.index; i++ ){
            final int position = i;

            reference = FirebaseDatabase.getInstance().getReference().child("purchases").child(position+"");


            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                    list.clear();
                    for (DataSnapshot snapshot: dataSnapshot1.getChildren()){
                        list.add(snapshot.getValue().toString());

                    }


                    //Log.d("Tag","Purchase list: "+list.get(1));
                    purchaseList.add(new Purchase(list.get(0), list.get(1), Double.parseDouble(list.get(2)), Integer.parseInt(list.get(3)),list.get(4)));
                    //Log.d("Tag","list: : "+purchaseList.get().returnStockSymbol());
                    customAdapter.notifyDataSetChanged();

              }




                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("Tag", databaseError.getMessage());
                }

            });
            //Log.d("Tag","Post Purchase list: "+purchaseList.get(0).returnStockSymbol());



        }
        //customAdapter.notifyDataSetChanged();





        return v;

    }



    public class CustomAdapter extends ArrayAdapter<Purchase> {

        Context context;
        List<Purchase> purchaseList;
        int xml;

        public CustomAdapter(@NonNull Context context, int resource, @NonNull List<Purchase> objects) {
            super(context, resource, objects);
            this.context = context;
            this.purchaseList = objects;
            xml = resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View adapterLayout = layoutInflater.inflate(xml, null);
            TextView companySymbol = adapterLayout.findViewById(R.id.id_companySymbol);
            TextView quantity = adapterLayout.findViewById(R.id.id_quantity);
            TextView purchasePrice = adapterLayout.findViewById(R.id.id_purchasePrice);
            TextView date = adapterLayout.findViewById(R.id.id_date);
            TextView buyOrSell = adapterLayout.findViewById(R.id.id_BuyOrSell);

            //Log.d("Tag",purchaseList.get(position).returnStockSymbol());
            Log.d("Tag","position"+position);

            companySymbol.setText(purchaseList.get(position).returnStockSymbol());
            quantity.setText(purchaseList.get(position).returnQuantity());
            purchasePrice.setText("$"+purchaseList.get(position).returnPrice());
            date.setText(purchaseList.get(position).returnDate());
            buyOrSell.setText(purchaseList.get(position).returnBuyOrSell());
            if(purchaseList.get(position).returnBuyOrSell().equals("BUY")) {
                buyOrSell.setTextColor(Color.GREEN);
            }
            else{
                buyOrSell.setTextColor(Color.RED);
            }
            buyOrSell.setTextSize(13);
            buyOrSell.setTypeface(buyOrSell.getTypeface(),Typeface.BOLD);
            quantity.setTypeface(quantity.getTypeface(),Typeface.BOLD);
            purchasePrice.setTypeface(purchasePrice.getTypeface(),Typeface.BOLD);
            date.setTypeface( date.getTypeface(),Typeface.BOLD);
            companySymbol.setTypeface(companySymbol.getTypeface(),Typeface.BOLD);


            return adapterLayout;
        }

    }

}

