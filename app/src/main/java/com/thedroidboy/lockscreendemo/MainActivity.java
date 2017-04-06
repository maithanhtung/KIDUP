package com.thedroidboy.lockscreendemo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yaakov Shahak on 14/12/2016.
 */

public class MainActivity extends Activity implements SensorEventListener {
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        startService(new Intent(this, LockScreenService.class));
//        finish();
//    }

    Button btnFinish;

    Button btnLock;

//    Button enable;

    public static DevicePolicyManager deviceManager;
    public static ComponentName compName;
    ActivityManager activityManager;
    static final int RESULT_ENABLE = 1;


    SensorManager sensorManager;


    TextView tv_steps;

    public static TextView textViewTime;

    boolean running = false;

    public static float steps = 0;
    float stepsInSensor = -1;
    public static float lastCount = 0;
    public static float timeLeft = 0;
    boolean timer_was_touched = false;
    public static float timeGot = 0;

    float timePause = 0;


    public static CounterClass timer = new CounterClass(0, 1000);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, LockScreenService.class));
//        finish();
        setContentView(R.layout.activity_main);

//        enable =(Button)findViewById(R.id.btnEnable);

        btnLock = (Button) findViewById(R.id.btnLock);

        deviceManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager)getSystemService(
                Context.ACTIVITY_SERVICE);
        compName = new ComponentName(this, MyAdmin.class);






        btnFinish = (Button) findViewById(R.id.btnFinish);


        textViewTime = (TextView) findViewById(R.id.textViewTime);

        textViewTime.setText("00:00:00");


        tv_steps = (TextView) findViewById(R.id.tv_steps);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);


        Log.d("last count",String.valueOf(lastCount));
        Log.d("Timeleft", String.valueOf(timeLeft));
        Log.d("Steps", String.valueOf(steps));

        btnLock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                boolean active = deviceManager.isAdminActive(compName);
//                if (active) {
//                    deviceManager.lockNow();
//                }
//                else {
//                    Log.d("tt","test");
//                }

                    boolean active = deviceManager.isAdminActive(compName);
                    if (active) {
                        deviceManager.lockNow();
                    }

            }
        });

//        enable.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DevicePolicyManager
//                        .ACTION_ADD_DEVICE_ADMIN);
//                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
//                        compName);
//                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
//                        "Additional text explaining why this needs to be added.");
//                startActivityForResult(intent, RESULT_ENABLE);
//            }
//        });


        btnFinish.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Log.d("last count BC",String.valueOf(lastCount));
                Log.d("Timeleft BC", String.valueOf(timeLeft));
                Log.d("Steps BC", String.valueOf(steps));

                timeGot = steps *  1000;
                timeLeft = timeLeft + timeGot;


                if(!timer_was_touched){
                    timer = new CounterClass((long)timeLeft,1000 );


                    timer.start();
                    timer_was_touched = true;
                    timeGot= 0;
                }
                else{
                    timer.cancel();
                    timer = new CounterClass((long)timeLeft,1000);

                    timer.start();
                    timer_was_touched = true;
                    timeGot= 0;
                }
                lastCount = lastCount + steps;
                tv_steps.setText("0");
                steps = 0;

            }
        });


        Intent intent = new Intent(DevicePolicyManager
                .ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                compName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Additional text explaining why this needs to be added.");
        startActivityForResult(intent, RESULT_ENABLE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("DeviceAdminSample", "Admin enabled!");
                } else {
                    Log.i("DeviceAdminSample", "Admin enable FAILED!");
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public static class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval){
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished){
            long millis = millisUntilFinished ;
            timeLeft = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d" , TimeUnit.MILLISECONDS.toHours(millis),TimeUnit.MILLISECONDS.toMinutes(millis)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)) );

            System.out.println(hms);
            textViewTime.setText(hms);
        }

        @Override
        public void onFinish(){


            textViewTime.setText("Out of time!");
            boolean active = deviceManager.isAdminActive(compName);
            if (active) {
                deviceManager.lockNow();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_steps.setText(String.valueOf(steps));

        long millis = (long) timeLeft;

        String hms = String.format("%02d:%02d:%02d" , TimeUnit.MILLISECONDS.toHours(millis),TimeUnit.MILLISECONDS.toMinutes(millis)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)) );
        textViewTime.setText(hms);
        Log.d("millis after unlock",String.valueOf(millis));
        Log.d("hms",hms);

//        timer = new CounterClass((long)timePause,1000 );
//        timer.start();
//        timer_was_touched = true;
//        timeGot= 0;
//        timePause = 0;
        running = true;

        Log.d("last count OR",String.valueOf(lastCount));
        Log.d("Timeleft OR", String.valueOf(timeLeft));
        Log.d("Steps OR", String.valueOf(steps));

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null){
            sensorManager.registerListener(this, countSensor,SensorManager.SENSOR_DELAY_UI);

        }
        else{
            Toast.makeText(this, "Sensor not found", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();


        timePause = timeLeft;
//        timer.cancel();
        running = true;
        Log.d("running OP",String.valueOf(running));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(stepsInSensor == -1){
            Log.d("event SC",String.valueOf(event.values[0]));
            stepsInSensor = event.values[0];
        }

        Log.d("running SC",String.valueOf(running));

        if(running){
            steps = event.values[0] - lastCount - stepsInSensor;
            tv_steps.setText(String.valueOf(steps));

            Log.d("event SC",String.valueOf(event.values[0]));
            Log.d("last count SC",String.valueOf(lastCount));
            Log.d("timeLeft SC", String.valueOf(timeLeft));
            Log.d("Steps SC", String.valueOf(steps));



        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static void startTimer(){
        Log.d("timer ",String.valueOf(timeLeft));
        timer = new CounterClass((long)timeLeft,1000 );
        timer.start();

    }

}
