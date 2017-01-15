package com.example.cj_sever.merchandisers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Created by cj-sever on 10/16/16.
 */
public  class  GeneralFunctions {

    public static void makeToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
    }
    public static void makeSnackbar(String msg, View view){
        Snackbar.make(view,msg,Snackbar.LENGTH_LONG).show();
    }


}
