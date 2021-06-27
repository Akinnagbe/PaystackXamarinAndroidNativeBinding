package com.olamidejames.paystackxamarinandroidlibrary;


import java.util.HashMap;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class ChargeCardModel extends CardModel {

    public  ChargeCardModel(){
        CustomFields = new HashMap<String, String>();
    }
    public   HashMap<String,String> CustomFields;
    public String currency;
    public int amount;
    public String email;
    public String reference;
    // public String callback_url;
    public  String plan;
    //public  String invoice_limit;
    public  String split_code;
    public  String subaccount;
    public int transaction_charge;
    public Charge.Bearer bearer;
}
