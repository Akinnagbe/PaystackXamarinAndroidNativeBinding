package com.olamidejames.paystackxamarinandroidlibrary;

import android.app.Activity;
import android.util.Log;

import org.json.JSONException;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class PaystackLibrary {


    private Charge charge;
    private  PaystackTransactionCallback transactionCallback;

    public static void init(Activity context){
        PaystackSdk.initialize(context.getApplicationContext());
    }

    public static void init(Activity context,String publicKey){
        PaystackSdk.initialize(context.getApplicationContext());
        PaystackSdk.setPublicKey(publicKey);
    }

    public static  void setPublicKey(String key){
        PaystackSdk.setPublicKey(key);
    }



    public void startFreshCharge(ChargeCardModel chargeCardModel,Activity activity,PaystackTransactionCallback callback){
        charge = new Charge();

        charge.setAmount(chargeCardModel.amount);
        charge.setBearer(chargeCardModel.bearer);
        charge.setCurrency(chargeCardModel.currency);
        charge.setEmail(chargeCardModel.email);
        if(!empty(chargeCardModel.subaccount)){
            charge.setPlan(chargeCardModel.plan);
        }

        charge.setReference(chargeCardModel.reference);
        if(!empty(chargeCardModel.subaccount)){
            charge.setSubaccount(chargeCardModel.subaccount);
        }

        charge.setTransactionCharge(chargeCardModel.transaction_charge);

        try {
            for (String i : chargeCardModel.CustomFields.keySet()) {
                charge.putCustomField(i, chargeCardModel.CustomFields.get(i));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        boolean isSetCardSuccessful = SetCard(chargeCardModel,callback);
        if(isSetCardSuccessful){
            chargeCard(activity);
        }

    }

    public void  ResumeCharge(Activity activity,ChargeCardModel chargeCardModel,PaystackTransactionCallback callback, String accessCode){
        charge = new Charge();
        boolean isSetCardSuccessful = SetCard(chargeCardModel,callback);
        if(isSetCardSuccessful){
            charge.setAccessCode(accessCode);
            chargeCard(activity);
        }
    }

    boolean SetCard(ChargeCardModel chargeCardModel,PaystackTransactionCallback callback){
        boolean isCardValid = false;

        transactionCallback = callback;

        String cardNumber = "4084084084084081";
        int expiryMonth = 11; //any month in the future
        int expiryYear = 25; // any year in the future. '2018' would work also!
        String cvv = "408";  // cvv of the test card

        Card card = new Card(chargeCardModel.PAN, chargeCardModel.ExpiryMonth, chargeCardModel.ExpiryYear, chargeCardModel.CVV);
        if (card.isValid()) {
            isCardValid = true;
            // charge card
            Log.d("paystack_binding", "charge card");
            charge.setCard(card);

        } else {
            Log.d("paystack_binding", "card is not valid");
        }

        return isCardValid;
    }

    public boolean IsCardValid(CardModel cardModel){
        Card card = new Card(cardModel.PAN, cardModel.ExpiryMonth, cardModel.ExpiryYear, cardModel.CVV);
        return card.isValid();
    }

    public boolean IsValidCVV(CardModel cardModel){
        Card card = new Card(cardModel.PAN, cardModel.ExpiryMonth, cardModel.ExpiryYear, cardModel.CVV);
        return card.validCVC();
    }

    public boolean IsValidExpiryDate(CardModel cardModel){
        Card card = new Card(cardModel.PAN, cardModel.ExpiryMonth, cardModel.ExpiryYear, cardModel.CVV);
        return card.validExpiryDate();
    }

    public boolean IsValidPAN(CardModel cardModel){
        Card card = new Card(cardModel.PAN, cardModel.ExpiryMonth, cardModel.ExpiryYear, cardModel.CVV);
        return card.validNumber();
    }

    public static boolean empty( final String s ) {
        // Null-safe, short-circuit evaluation.
        return s == null || s.trim().isEmpty();
    }

    private  void chargeCard(Activity activity){

        try {

            PaystackSdk.chargeCard(activity, charge, new Paystack.TransactionCallback() {
                @Override
                public void onSuccess(Transaction transaction) {
                    // This is called only after transaction is deemed successful.
                    // Retrieve the transaction, and send its reference to your server
                    // for verification.

                    String referenceId = transaction.getReference();
                    transactionCallback.onSuccess(referenceId);

                    Log.d("paystack_binding", "payment reference is: " + referenceId);
                }

                @Override
                public void beforeValidate(Transaction transaction) {
                    // This is called only before requesting OTP.
                    // Save reference so you may send to server. If
                    // error occurs with OTP, you should still verify on server.
                }

                @Override
                public void onError(Throwable error, Transaction transaction) {
                    //handle error here
                    transactionCallback.onError(error,transaction.getReference());
                    Log.d("paystack_binding", error.toString());
                }

            });
        }
        catch(Exception e) {
            Log.d("paystack_binding", e.getMessage());
        }

    }
}
