package com.thedroidboy.lockscreendemo;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by t3math00 on 4/5/2017.
 */


public class LockScreenService extends Service implements View.OnClickListener {


    private LinearLayout linearLayout;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;



    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenReceiver, intentFilter);
        windowManager = ((WindowManager) getSystemService(WINDOW_SERVICE));
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN //draw on status bar
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,// hiding the home screen button
                PixelFormat.TRANSLUCENT);
        Log.d("steps LS",String.valueOf(MainActivity.steps));


    }



    private void init() {
        linearLayout = new LinearLayout(this);
        windowManager.addView(linearLayout, layoutParams);
        ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.lock_screen, linearLayout);
        View btnClose = linearLayout.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(this);
        View btnEarnTime = linearLayout.findViewById(R.id.btnFinish);
//        btnEarnTime.setOnClickListener(this);

        btnEarnTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Log.d("working", "earn");
                Log.d("steps LSbtn",String.valueOf(MainActivity.steps));
                MainActivity.timeGot = MainActivity.steps *  10000;
                MainActivity.timeLeft = MainActivity.timeLeft + MainActivity.timeGot;


                MainActivity.lastCount = MainActivity.lastCount + MainActivity.steps;
                MainActivity.steps = 0;

                Log.d("steps after LSbtn",String.valueOf(MainActivity.steps));
                Log.d("timegot after LSbtn",String.valueOf(MainActivity.timeGot));
                Log.d("timeleft after LSbtn",String.valueOf(MainActivity.timeLeft));
                Log.d("lastcount after LSbtn",String.valueOf(MainActivity.lastCount));
            }
        });
    }

    @Override
    public void onClick(View view) {

//        if(MainActivity.timeLeft == 0) {
//            AlertDialog alertDialog = new AlertDialog.Builder(LockScreenService.this).create();
//            alertDialog.setTitle("Alert");
//            alertDialog.setMessage("Alert message to be shown");
//            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            alertDialog.show();
//        }
        if (MainActivity.timeLeft >0 ){

            MainActivity.startTimer();

            windowManager.removeView(linearLayout);
            linearLayout = null;
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(screenReceiver);
        super.onDestroy();
    }

    BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF) && linearLayout == null) {
                init();
            }
        }
    };


}
