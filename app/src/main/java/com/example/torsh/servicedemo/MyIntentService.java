package com.example.torsh.servicedemo;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by torsh on 4/1/17.
 * This class is for getting data back with help of ResultReceiver
 *
 * An IntentService subclass
 * The class IntentService has the default implementation of onStartCommand(), onBind(Intent)
 * -> meaning we don't have to override it here
 *
 * IntentService:
 * creates a default worker thread that executes all intents delivered to onStartCommand() separate from main thread.
 * creates a work queue that passes one intent at a time to the onHandleIntent()
 *  - implementation, so no need of worrying about multi-threading.
 * Stops the service automatically by stopSelf() after all start requests have been handled
 * Provides default implementation of onBind() that returns null.
 * Provides a default implementation of onStartCommand() that sends the intent to the work queue
 *  - and then to the onHandleIntent() implementation.
 *
 */

public class MyIntentService extends IntentService {


    private final String TAG = MyIntentService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyIntentService() {
        // in the super giving the name of our background thread: 'MyWorkerThread'
        super("MyWorkerThread");
    }

    // overriding onCreate and onDestroy to get the idea
    // of what is going on in our MyIntentService class
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "MyIntentService->onCreate(), Thread name " + Thread.currentThread().getName());
    }

    // onHandleIntent works in the worker thread
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "MyIntentService->onHandleIntent(), Thread name " + Thread.currentThread().getName());

        // onHandleIntent receives int 10 from the main activity
        int sleepTime = intent.getIntExtra("sleepTime", 1);

        // retrieve our parcelable receiver here:
        ResultReceiver resultReceiver = intent.getParcelableExtra("receiver");

        // this long dummy task is copied from doIBackground
        int counter = 1;
        while ( counter <= sleepTime) {
            Log.i(TAG, "counter is now: " + counter);
            // publishProgress triggers onProgressUpdate

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            counter++;
        }
        // after operation is finished return the result to the activity
        Bundle bundle = new Bundle();
        bundle.putString("resultIntentService", "counter stopped at " + counter + " seconds");
        resultReceiver.send(18, bundle); // this method triggers onReceiveResult inside MyResultReceiver
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MyIntentService->onDestroy(), Thread name " + Thread.currentThread().getName());
    }
}
