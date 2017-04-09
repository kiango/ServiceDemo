package com.example.torsh.servicedemo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.torsh.servicedemo.services.MyIntentService;
import com.example.torsh.servicedemo.services.MyStartService;
import com.example.torsh.servicedemo.R;

public class MainActivity extends AppCompatActivity {

    private TextView txtvStartServiceResult, txtvIntentServiceResult;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtvStartServiceResult = (TextView) findViewById(R.id.txtvSSR);
        txtvIntentServiceResult = (TextView) findViewById(R.id.txtvISR);
    }

    // start service and declare it in service tag in the manifest-file.
    public void startService(View view) {

        Intent intent = new Intent(MainActivity.this, MyStartService.class);
        // pass intent with key value.
        // These parameter wil be received by MyStartService.onStartCommand
        intent.putExtra("sleepTime", 10);
        startService(intent);
    }

    public void stopService(View view) {

        // explicit intent: it is only related to this MainActivity and MyStartService.class
        Intent intent = new Intent(MainActivity.this, MyStartService.class);
        stopService(intent);
    }

    public void startIntentService(View view) {
        //initialize MyResultReceiver with null (without handle)
        ResultReceiver myResultReceiver = new MyResultReceiver(null);


        Intent intent = new Intent(this, MyIntentService.class);
        intent.putExtra("sleepTime", 10);
        // pass the myResultReceiver with intent. myResultReceiver is a parcelable object
        intent.putExtra("receiver", myResultReceiver);
        startService(intent); // this intent will be sent to the worker thread of MyIntentService
        // onHandleIntent() receives integer value of '10'.
    }


    // dynamically register myStartedServiceBroadcastReceiver in onResume
    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action.service.to.activity");
        registerReceiver(myStartedServiceBroadcastReceiver, intentFilter);
    }

    // when the app close unregister our broadcast receiver
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myStartedServiceBroadcastReceiver);
    }

    public void move2SecondActivity(View view){
        Intent intent = new Intent(this, MyBoundActivity.class);
        startActivity(intent);
    }

    // creating BroadcastReceiver dynamically -> make intent filter dynamically (above)
    private BroadcastReceiver myStartedServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // now extracting the received string from intent
            String result = intent.getStringExtra("startServiceResult");
            // update the text view with it
            txtvStartServiceResult.setText(result);

        }
    };

    public void move2MessengerActivity(View view) {
        Intent intent = new Intent(this, MyMessengerActivity.class);
        startActivity(intent);
    }


    // To receive the data back from MyIntentService.java using ResultReceiver
    // using MyResultReceiver to get data back from application component
    // for use of MyIntentService class to update the UI,
    // we need to create a subclass of result receiver here:
    private class MyResultReceiver extends ResultReceiver{

        // onReceiveResult method works in worker thread
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            Log.i("MyResultReceiver ", Thread.currentThread().getName());

            // using the resultCode and resultData
            if (resultCode == 18 && resultData != null){
                final String result = resultData.getString("resultIntentService");

                // since we are in worker thread, text view UI cannot be updated
                // there for using a handel for the update of text view
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // this method executes in the main thread
                        Log.i("MyHandler ", Thread.currentThread().getName());
                        txtvIntentServiceResult.setText(result);
                    }
                });


            }
        }

        /**
         * Create a new ResultReceive to receive results.  Your
         * {@link #onReceiveResult} method will be called from the thread running
         * <var>handler</var> if given, or from an arbitrary thread if null.
         *
         * @param handler
         */
        // this call back method is responsible to receive data from intent service class
        public MyResultReceiver(Handler handler) {
            super(handler);


        }
    }
}
