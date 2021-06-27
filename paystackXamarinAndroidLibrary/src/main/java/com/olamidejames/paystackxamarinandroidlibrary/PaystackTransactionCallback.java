package com.olamidejames.paystackxamarinandroidlibrary;

import co.paystack.android.Paystack;
import co.paystack.android.Transaction;

public interface PaystackTransactionCallback  {

    void onSuccess(String referenceId);

    void onError(Throwable error, String referencId);
}