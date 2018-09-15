package com.example.root.wifi1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ConnectivityManager mComMgr;

    public NetworkReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting view content as activity_main.xml
        setContentView(R.layout.activity_main);

        //store connectivity manager in member variable
        mComMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //instantiate network events as broadcast receiver
        mReceiver= new NetworkReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        //register broadcast with intent action CONNECTIVITY_ACTION
        //broadcast receiver's nnReceive will be called every time a network occurs
        registerReceiver(mReceiver, filter);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();

        //unregister broadcast receiver during background activity
        if(mReceiver != null){
            unregisterReceiver(mReceiver);
        }
    }

    //Network status button event handler
    public void onShowNetworkStatus(View v) {
        //make sure connection manager instance is not null variable
        if(mComMgr != null){
            //active network info struct
            NetworkInfo networkInfo = mComMgr.getActiveNetworkInfo();

            //check if active network interface has internet connection available
            if(networkInfo != null){

                //check if active network is Wifi
                boolean isWifi = mComMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();

                //check if active one is mobile data
                boolean isGSM = mComMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();

                //check if bluetooth is connected
                //boolean isBTAvailable = mComMgr.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH).isConnected();

                if( networkInfo.isConnected() ) {
                    if( isWifi ){
                        //display toast message that network is active and connected through WiFi connection
                        Toast.makeText(this, "Network is Available by WiFi Connection", Toast.LENGTH_SHORT).show();
                    } else if( isGSM ){
                        //display toast message that network is active and connected through GSM connection
                        Toast.makeText(this, "Network is Available by GSM/Mobile Data Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            } else{
                //display toast message that network is not active
                Toast.makeText(this, "Network is Currently Not Available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*broadcast receiver which onReceive will be called whenever a network event
    such as network disconnected or network connected takes place
    The broadcast receiver is registered in the onCreate with intent action CONNECTIVITY_ACTION*/
    public class NetworkReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent){

            //get network info structure
            NetworkInfo networkInfo = mComMgr.getActiveNetworkInfo();

            if(networkInfo != null){
                //check if active network is Wifi
                boolean isWifiAvailable = mComMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();

                //check if active one is mobile data
                boolean isGSMAvailable = mComMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();

                //check if bluetooth is connected
                //boolean isBTAvailable = mComMgr.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH).isConnected();

                if(isWifiAvailable){
                    //Display wifi connected as toast
                    Toast.makeText(context, "WiFi is connected", Toast.LENGTH_SHORT).show();
                } else if(isGSMAvailable){
                    //Display GSM connected as toast
                    Toast.makeText(context, "GSM data is connected", Toast.LENGTH_SHORT).show();
                } else {
                    //Display network not available as toast
                    Toast.makeText(context, "Not connected/Not Available", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
