package lk.javainstitute.automotivex.broadcasts;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.widget.Toast;

import lk.javainstitute.automotivex.WelcomeActivity;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

            if (wifiState == WifiManager.WIFI_STATE_DISABLED) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Connection Lost!");
                builder.setMessage("No internet connection detected. Please check your Wi-Fi or mobile data and try again.");

                // OK Button
                builder.setPositiveButton("Turn On Wifi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        context.startActivity(intent);
                    }
                });

                // Show the AlertDialog
                AlertDialog alert = builder.create();
                alert.show();

            }
        }





    }
}
