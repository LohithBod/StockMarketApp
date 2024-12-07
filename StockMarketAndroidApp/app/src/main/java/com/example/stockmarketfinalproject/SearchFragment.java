package com.example.stockmarketfinalproject;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.Objects;

public class SearchFragment extends Fragment {

    Button searchButton;
    TextView currentStockPrice;
    EditText editText;
    EditText buyQuantity;
    EditText sellQuantity;
    String companySymbolUserSearched;
    TextView sharesOwned;
    TextView marketValue;
    Button buyButton;
    Button sellButton;
    TextView companyName;
    TextView buyingPower;
    String string1;
    String string2;
    String companyNameDuringBuy;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    DatabaseReference mDatabase;
    String tempN;
    int tempQ;
    int shares;
    double price;
    int quantityBought;
    boolean flag = false;
    boolean flag2 = false;
    double deductionAmount;
    int position;
    int quantitySold;
    int tempParallelArrIndex;
    double tempMarketVal;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_search, container, false);

        //assigning values
        editText = v.findViewById(R.id.id_fragmentOne_plainText);
        currentStockPrice = v.findViewById(R.id.id_currentPricePerShare);
        searchButton = v.findViewById(R.id.id_searchButton);
        sharesOwned = v.findViewById(R.id.id_sharesOwned);
        marketValue = v.findViewById(R.id.id_marketValue);
        buyButton = v.findViewById(R.id.id_buyShareButton);
        sellButton = v.findViewById(R.id.id_sellShareButton);
        companyName = v.findViewById(R.id.id_companyName);
        buyQuantity = v.findViewById(R.id.id_buyQuantity);
        sellQuantity = v.findViewById(R.id.id_sellQuantity);
        buyingPower = v.findViewById(R.id.id_moneyLeft);
        shares = 0;
        tempMarketVal = 0;
        tempParallelArrIndex = MainActivity.parallelArrCount;
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("purchases");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("purchases").child("0");


        //money = Double.parseDouble(reference.child("0").child("price"));
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DecimalFormat df = new DecimalFormat("#.##");
                buyingPower.setText("Buying Power:        $"+df.format(MainActivity.money));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Calendar calendar = Calendar.getInstance();
        final String currentTime = DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT).format(calendar.getTime());
        //final Date currentTime = Calendar.getInstance(DateFormat.).getTime();






        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                companySymbolUserSearched = (editText.getText().toString());
                if(companySymbolUserSearched == null || companySymbolUserSearched.equals("")){
                    Toast.makeText(getContext(),"Please Enter A Symbol", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        URL url = new URL("https://finnhub.io/api/v1/quote?symbol="+companySymbolUserSearched+"&token=c2ipsf2ad3i8gi7prq30");
                        downloadOnlineFile onlineFile = new downloadOnlineFile();
                        onlineFile.execute(url);
                        //Log.d("TAG", url.toString());


                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    try {
                        URL url = new URL("https://finnhub.io/api/v1/search?q="+companySymbolUserSearched+"&token=c2ipsf2ad3i8gi7prq30");
                        downloadOnlineFile onlineFile = new downloadOnlineFile();
                        onlineFile.execute(url);
                        //Log.d("TAG", url.toString());

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }




            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                companyNameDuringBuy = editText.getText().toString();
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("purchases");

                if((buyQuantity.getText().toString().equals(null) && companyNameDuringBuy.equals("")) || (buyQuantity.getText().toString().equals("") && companyNameDuringBuy.equals(""))) {
                    Toast.makeText(getContext(),"Please Enter A Quantity To Buy And A Stock Symbol", Toast.LENGTH_SHORT).show();
                }
                else if(buyQuantity.getText().toString().equals(null) || buyQuantity.getText().toString().equals("")){
                    Toast.makeText(getContext(),"Please Enter A Quantity To Buy", Toast.LENGTH_SHORT).show();
                }
                else{
                    quantityBought = Integer.parseInt(buyQuantity.getText().toString());
                    MainActivity.index++;
                    reference.child("0").child("index").setValue(MainActivity.index);

                    reference.child(MainActivity.index+"").child("Price").setValue(price);
                    reference.child(MainActivity.index+"").child("Qty").setValue(quantityBought);
                    reference.child(MainActivity.index+"").child("Name").setValue(companyNameDuringBuy);
                    reference.child(MainActivity.index+"").child("Date").setValue(currentTime);
                    reference.child(MainActivity.index+"").child("Sell or Buy").setValue("BUY");
                    deductionAmount = price * quantityBought;
                    if(MainActivity.money >= deductionAmount) {
                        MainActivity.money = MainActivity.money - deductionAmount;
                        reference.child("0").child("price").setValue(MainActivity.money);
                        Toast.makeText(getContext(),"Transaction Complete", Toast.LENGTH_SHORT).show();


                        reference.addListenerForSingleValueEvent(new ValueEventListener(){
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(int i = 0; i<MainActivity.parallelArrCount; i++) {
                                    position = i;
                                    tempN = Objects.requireNonNull(dataSnapshot.child(0 + "").child("stocks").child(position + "").getValue()).toString();
                                    Log.d("Tag", "TempN:" + tempN);
                                    tempQ = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(0 + "").child("amount").child(position + "").getValue()).toString());
                                    Log.d("Tag", "TempQ:" + tempQ);
                                    if (companyNameDuringBuy.equals(tempN)) {
                                        reference.child("0").child("amount").child(position + "").setValue(quantityBought + tempQ);
                                        flag = true;
                                    }
                                }
                                if(flag == false){
                                    reference.child("0").child("stocks").child(MainActivity.parallelArrCount+"").setValue(companyNameDuringBuy);
                                    reference.child("0").child("amount").child(MainActivity.parallelArrCount+"").setValue(quantityBought);
                                    reference.child("0").child("current value").child(MainActivity.parallelArrCount+"").setValue(0);
                                    MainActivity.parallelArrCount++;
                                    reference.child("0").child("parallel array count").setValue(MainActivity.parallelArrCount);
                                }
                                flag = false;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }else{
                        reference.child(MainActivity.index+"").removeValue();
                        MainActivity.index--;
                        reference.child("0").child("index").setValue(MainActivity.index);
                        Toast.makeText(getContext(),"Insufficient Funds", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                companyNameDuringBuy = editText.getText().toString();
                if((sellQuantity.getText().toString().equals(null) && companyNameDuringBuy.equals("")) || (sellQuantity.getText().toString().equals("") && companyNameDuringBuy.equals(""))) {
                    Toast.makeText(getContext(),"Please Enter A Quantity To Sell And A Stock Symbol", Toast.LENGTH_SHORT).show();
                }
                else if(sellQuantity.getText().toString().equals(null) || sellQuantity.getText().toString().equals("")){
                    Toast.makeText(getContext(),"Please Enter A Quantity To Sell", Toast.LENGTH_SHORT).show();
                }
                else{
                    quantitySold = Integer.parseInt(sellQuantity.getText().toString());
                    reference.addListenerForSingleValueEvent(new ValueEventListener(){
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(int i = 0; i<MainActivity.parallelArrCount; i++) {
                                position = i;
                                tempN = Objects.requireNonNull(dataSnapshot.child(0 + "").child("stocks").child(position + "").getValue()).toString();
                                Log.d("Tag", "TempN:" + tempN);
                                tempQ = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child(0 + "").child("amount").child(position + "").getValue()).toString());
                                if (companyNameDuringBuy.equals(tempN)) {
                                    if(quantitySold <= tempQ){
                                        reference.child("0").child("amount").child(position + "").setValue(tempQ - quantitySold);
                                        Toast.makeText(getContext(),"Transaction Complete", Toast.LENGTH_SHORT).show();
                                        tempQ = tempQ - quantitySold;
                                        MainActivity.index++;
                                        MainActivity.money += (price*quantitySold);
                                        reference.child("0").child("price").setValue(MainActivity.money);
                                        reference.child("0").child("index").setValue(MainActivity.index);
                                        reference.child(MainActivity.index+"").child("Price").setValue(price);
                                        reference.child(MainActivity.index+"").child("Qty").setValue(quantitySold);
                                        reference.child(MainActivity.index+"").child("Name").setValue(companyNameDuringBuy);
                                        reference.child(MainActivity.index+"").child("Date").setValue(currentTime);
                                        reference.child(MainActivity.index+"").child("Sell or Buy").setValue("SELL");
                                        flag2 = true;
                                    }
                                    else if(tempQ != 0){
                                        Toast.makeText(getContext(),"Not Enough Shares", Toast.LENGTH_SHORT).show();
                                        flag2 = true;
                                    }
                                }
                            }
                            if(!flag2){
                                Toast.makeText(getContext(),"You Do Not Own This Stock", Toast.LENGTH_SHORT).show();
                            }
                            flag2 = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });

        return v;
    }

    class downloadOnlineFile extends AsyncTask<URL,Void, JSONObject> {

        JSONObject object;
        JSONObject object2;
        String info;
        String info2;


        protected JSONObject doInBackground(URL...urls){
            try {
                HttpURLConnection urlConnection = (HttpURLConnection)urls[0].openConnection();
                InputStream urlInputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlInputStream));
                info = reader.readLine();
                object = new JSONObject(info);
                return  object;

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                HttpURLConnection urlConnection = (HttpURLConnection)urls[1].openConnection();
                InputStream urlInputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlInputStream));
                info2 = reader.readLine();
                object2 = new JSONObject(info2);
                return object2;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(JSONObject object){
            try {
                //Sets TextView to be Current Price of user's searched stock
                price = Double.parseDouble(object.getString("c"));
                string1 = ("Current Price Per Share: $"+ object.getString("c"));


            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                //Sets TextView to be name of user's searched symbol
                string2 = (object.getJSONArray("result").getJSONObject(0).getString("description"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            currentStockPrice.setText(string1);
            companyName.setText(string2);
            //companySymbolUserSearched = (editText.getText().toString());
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(MainActivity.parallelArrCount == 0){
                        sharesOwned.setText("Shares Owned: 0");
                        marketValue.setText("Market Value: $0");
                    }
                    for(int i = 0; i<MainActivity.parallelArrCount; i++){
                        if(companySymbolUserSearched.equals(Objects.requireNonNull(snapshot.child("stocks").child(i + "").getValue()).toString())){
                            shares = Integer.parseInt(Objects.requireNonNull(snapshot.child("amount").child(i + "").getValue()).toString());
                            sharesOwned.setText("Shares Owned: "+shares);
                            tempMarketVal = shares*price;
                            DecimalFormat df = new DecimalFormat("#.##");
                            marketValue.setText("Market Value: $"+df.format(tempMarketVal));
                            //Log.d("Tag","shares"+shares);
                            Log.d("Tag","shares"+shares*price);
                            i = MainActivity.parallelArrCount;
                        }
                        else{
                            sharesOwned.setText("Shares Owned: 0");
                            marketValue.setText("Market Value: $0");

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }



    }


    public void saveUsersShares(){
        String name = companySymbolUserSearched;
        //FirebaseUser user = firebaseAuth.getCurrentUser();
    }


}