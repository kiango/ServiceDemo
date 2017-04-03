package com.example.torsh.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by torsh on 4/2/17.
 */

public class MyBoundService extends Service {

    private MyLocalBinder myLocalBinder = new MyLocalBinder();

    // creating a local Binder object to help activity to bind its service
    // MyLocalBinder should return the instance of MyBoundService.java
    public class MyLocalBinder extends Binder{
        MyBoundService getService(){
            return MyBoundService.this;
        }
    }

    // onBind called by bindService from the activity
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("onBind ", Thread.currentThread().getName()); // runs on the main thread
        //return null; // should never return null. It must return the Binder objects
        return myLocalBinder;
    }

    public int add(int a, int b){
        return a+b;
    }

    public int subtract(int a, int b){
        return a-b;
    }

    public int multiply(int a, int b){
        return a*b;
    }

    public float divide(int a, int b){
        return (float)a/(float)b;
    }
}
