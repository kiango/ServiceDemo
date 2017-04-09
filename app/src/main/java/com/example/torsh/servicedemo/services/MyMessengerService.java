package com.example.torsh.servicedemo.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by torsh on 4/4/17.
 * MyMessengerService is a bound service
 * this services has its own process
 */

public class MyMessengerService extends Service{

    // create a messenger with a Handle object in the constructor
    Messenger messenger = new Messenger( new IncomingHandler() );

    // handler manages messages between the threads, coming from MyMessengerActivity / another process
    private class IncomingHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                // what operation to perform in service class, returns int constant
                case 43:
                    Bundle bundle = msg.getData();
                    int numOne = bundle.getInt("numOne", 0);
                    int numTwo = bundle.getInt("numTwo", 0);
                    int result = addNumbers(numOne, numTwo);
                    Toast.makeText(getApplicationContext(), "Result: " + result, Toast.LENGTH_SHORT).show();
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    // onBind always returns a Binder object
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder(); // bridge the communication gap between the activity and Binder
    }

    // this method will be exposed to MyMessengerActivity
    // when the connection between MyMessengerActivity and MyMessengerService
    public int addNumbers(int a, int b){
        return a+b;
    }
}
