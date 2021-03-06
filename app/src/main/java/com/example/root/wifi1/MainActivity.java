package com.example.root.wifi1;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;

public class MainActivity extends Activity {


    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean mRequestLocUpdate = false;
    private LocationRequest mLocRequest;
    private GoogleApiClient mClient;
    private Location lasLoc;
    private TextView StrLatLong, txtCoordinate;
    private Button btnGetLoc;
    double latShow;
    double longShow;
    String latShowStr;
    String longShowStr;

    private ConnectivityManager mComMgr;
    public NetworkReceiver mReceiver;

    Button get_unique_code;

    TextView print_unique_code;

    String server_url = "http://www.mocky.io/v2/5bc664273200004e000b0329";//insert the url or server address here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting view content as activity_main.xml
        setContentView(R.layout.activity_main);

        //StrLatLong = findViewById(R.id.textView2);

        txtCoordinate = (TextView) findViewById(R.id.textView2);
        btnGetLoc = (Button) findViewById(R.id.button2);

        //location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latShow = location.getLatitude();
                longShow = location.getLongitude();
                latShowStr = Double.toString(latShow);
                longShowStr = Double.toString(longShow);
                StrLatLong.append("\nLat = " + latShowStr + "\nLong = " + longShowStr);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        //}
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 0, locationListener);/* update every 60000 miliseconds
        and if the person has moved 0 meter*/

        //store connectivity manager in member variable
        mComMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //instantiate network events as broadcast receiver
        mReceiver= new NetworkReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

        //register broadcast with intent action CONNECTIVITY_ACTION
        //broadcast receiver's nnReceive will be called every time a network occurs
        registerReceiver(mReceiver, filter);

        get_unique_code = findViewById(R.id.get_unique_code_button);
        print_unique_code = findViewById(R.id.unique_code);

        get_unique_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send request by http
                final RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,

                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                print_unique_code.setText("Your Unique Code is " + response);
                                requestQueue.stop();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        print_unique_code.setText("Error to receive Unique Code");
                        error.printStackTrace();
                        requestQueue.stop();
                    }
                });
                requestQueue.add(stringRequest);
            }
        });
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
