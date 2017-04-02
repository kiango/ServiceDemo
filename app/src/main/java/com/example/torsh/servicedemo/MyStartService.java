package com.example.torsh.servicedemo;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by torsh on 3/27/17.
 */

public class MyStartService extends Service {

    private static final String TAG = MyStartService.class.getSimpleName();

    // override onCreate, onStartCommand, onBind and onDestroy methods in this class
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate, Thread name: " + Thread.currentThread().getName());
    }

    // the service of MainActivity.startService get triggered and started by onStartCommand
    // onStartCommand starts AsyncTask
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand, Thread name: " + Thread.currentThread().getName());


        // a dummy long running task makes UI to NoT respond
        int sleepTime = intent.getIntExtra("sleepTime", 1);
//        try {
//            Thread.sleep(sleepTime*1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        new MyAsyncTask().execute(sleepTime); // passing sleepTime to AsyncTask

        // perform service task, only short non-blocking UI thread tasks
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY; // intent becomes null
        //return START_REDELIVER_INTENT; // service restarts with intent

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind, Thread name: " + Thread.currentThread().getName());
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy, Thread name: " + Thread.currentThread().getName());
    }

    // parameters Void, Void, Void must by aligned with doInBack, onProgress, onPostExe
    class MyAsyncTask extends AsyncTask<Integer, String, String>{

        private final String TAG = MyAsyncTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

                Log.i(TAG, "onPreExecute() Thread name: " + Thread.currentThread().getName());
        }


        // only doInBackground works in background thread, others in the work thread!
        // perform long running task
        // no toast messages from the background method
        @Override
        protected String doInBackground(Integer... params) {

            Log.i(TAG, "doInBackground() Thread name: " + Thread.currentThread().getName());

            // dummy long operation
            int sleepTime = params[0];
            int counter = 1;
            while ( counter <= sleepTime){
                publishProgress("counter is now: " + counter);
                // publishProgress triggers onProgressUpdate

                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                counter++;
            }
            return "counter stopped at " + counter + "sec. given by BroadRec.";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Toast.makeText(MyStartService.this,values[0], Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Counter value"+ values[0] + "onProgressUpdate() Thread name: " + Thread.currentThread().getName());
        }

        // onPostExecute receives values from doInBackground
        // runs in the main thread
        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);

            stopSelf(); // automatically destroy the service from the service class itself
            // instead of pushing the button from the main UI

            Log.i(TAG, "onPostExecute() Thread name: " + Thread.currentThread().getName());

            // broadcast an intent that will be received by the BroadcastReceiver in the activity
            Intent intent = new Intent("action.service.to.activity");
            intent.putExtra("startServiceResult", str);
            sendBroadcast(intent); // when this is send the Activity get it by myStartedServiceReceiver
        }
    }
}
