package com.example.torsh.servicedemo.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.torsh.servicedemo.R;
import com.example.torsh.servicedemo.services.MyMessengerService;

/**
 * Created by torsh on 4/4/17.
 */

public class MyMessengerActivity extends AppCompatActivity {

    private TextView txtViewResult;
    // to indicate if the connection between activity and service is established
    private Boolean serviceConnectionIsBound = false;
    private Messenger mService = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        // onServiceConnected will receive a Binder from onBind method of service object
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // getting reference to messenger object in MyMessengerService with the help of getBinder()
            mService = new Messenger(service);

            serviceConnectionIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceConnectionIsBound = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        txtViewResult = (TextView) findViewById(R.id.txtvResult2);
    }


    public void performAddOperation(View view) {

        if (mService == null){
            Toast.makeText(getApplicationContext(), "Bind Service first!", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText edTxtNum1 = (EditText) findViewById(R.id.edt1);
        EditText edTxtNum2 = (EditText) findViewById(R.id.edt2);

        int num1 = Integer.valueOf(edTxtNum1.getText().toString());
        int num2 = Integer.valueOf(edTxtNum2.getText().toString());

        // send data from activity to the service
        Message msgToService = Message.obtain(null, 43);

        Bundle bundle = new Bundle();
        bundle.putInt("numOne",num1);
        bundle.putInt("numTwo",num2);

        msgToService.setData(bundle);
        try {
            mService.send(msgToService); // it triggers the IncomingHandler.handelMessage
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // when bindService executed MyMessengerService.onBind called and return a binder object
    public void bindService(View view) {
        Intent intent = new Intent(this, MyMessengerService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    public void unbindService(View view) {

        if (serviceConnectionIsBound){
            unbindService(serviceConnection);
            serviceConnectionIsBound = false;
        }
    }
}
