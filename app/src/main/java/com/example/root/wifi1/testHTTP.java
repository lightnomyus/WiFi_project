package com.example.root.wifi1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class testHTTP extends Activity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setting view content as activity_main.xml
        setContentView(R.layout.test_http);

        //store connectivity manager in member variable
        //mComMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //image view reference
        //imageView = (ImageView)findViewById(R.id.resultImage);
    }

    public void onPreviousButtonClick(View v){
        Intent first_page = new Intent(testHTTP.this, MainActivity.class);
        startActivity(first_page);
    }
}
