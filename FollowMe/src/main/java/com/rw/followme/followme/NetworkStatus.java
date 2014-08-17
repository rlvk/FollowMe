package com.rw.followme.followme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by rafalwesolowski on 06/04/2014.
 */
public class NetworkStatus {

    public static int NOT_CONNECTED = 0;
    public static int CONNECTED = 1;
    public static ConnectivityManager connectManager;
    private static AlertDialog alertDialog;

    public static int getConnectivityStatus(Context context) {
        int result = 0;
        connectManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectManager.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if(activeNetwork.isConnected())
                result = CONNECTED;
            else
                result = NOT_CONNECTED;
        }

        return result;
    }

    public static void showDialogAlert(Context context, String title ,String message){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        alertDialog.show();
    }
}
