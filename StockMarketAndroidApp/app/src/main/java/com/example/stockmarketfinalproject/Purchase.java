package com.example.stockmarketfinalproject;

public class Purchase
{
    double price;
    int quantity;
    String stockSymbol;
    String date;
    String bORs;

    public Purchase(String d, String ss, double p, int q, String bORs){
        this.price = p;
        this.quantity = q;
        this.stockSymbol = ss;
        this.date = d;
        this.bORs = bORs;


    }

    public String returnPrice(){
        return price+"";
    }

    public String returnQuantity(){
        return quantity+"";
    }

    public String returnStockSymbol(){
        return stockSymbol;
    }

    public String returnDate(){
        return date;
    }

    public String returnBuyOrSell(){
        return bORs;
    }

}
