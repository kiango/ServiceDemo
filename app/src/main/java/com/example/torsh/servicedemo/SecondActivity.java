package com.example.torsh.servicedemo;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by torsh on 4/2/17.
 *
 * Second Activity has calling components that want some services from MyBoundService
 * We connect these 2 classes by creating ServiceConnection class
 */

public class SecondActivity extends AppCompatActivity {

    Boolean isBound = false;
    private MyBoundService myBoundService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        // onServiceConnected will be executed when connection between 2activity and MyBoundService established
        // when receives myLocalBinder from MyBoundService (as iBinder or service)
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            // extracting myLocalBinder from iBinder
            MyBoundService.MyLocalBinder myLocalBinder = (MyBoundService.MyLocalBinder) iBinder;
            myBoundService = myLocalBinder.getService(); // returns the instance of myBoundService
            isBound = true; // when service is connected
        }

        // onServiceDisconnected will be executed when between 2activity and MyBoundService unbinds
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false; // when service is not connected
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // when activity starts we invoke the service by bindService to MyBoundService:
        Intent intent = new Intent(this, MyBoundService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE); // use bindService instead of start service
    }

    // we ave to unbind the service when we get out of the second activity e.g. by back button
    @Override
    protected void onStop() {
        super.onStop();

        if (isBound){
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    public void onClickEvent(View view) {

        EditText edText1 = (EditText) findViewById(R.id.editText1);
        EditText edText2 = (EditText) findViewById(R.id.editText2);
        TextView textViewResult = (TextView) findViewById(R.id.txtvResult);

        int numOne = Integer.valueOf(edText1.getText().toString());
        int numTwo = Integer.valueOf(edText2.getText().toString());

        //Log.i("numOne", String.valueOf(numOne) + String.valueOf(numTwo));

        String resultStr = "";

        if (isBound) {
            switch (view.getId()){
                case R.id.btnAdd:
                    resultStr = String.valueOf(myBoundService.add(numOne, numTwo));
                    break;
                case R.id.btnSubtract:
                    resultStr = String.valueOf(myBoundService.subtract(numOne, numTwo));
                    break;
                case R.id.btnMultiply:
                    resultStr = String.valueOf(myBoundService.multiply(numOne, numTwo));
                    break;
                case R.id.btnDivide:
                    resultStr = String.valueOf(myBoundService.divide(numOne, numTwo));
                    break;
            }
            textViewResult.setText(resultStr);
        }
    }
}
