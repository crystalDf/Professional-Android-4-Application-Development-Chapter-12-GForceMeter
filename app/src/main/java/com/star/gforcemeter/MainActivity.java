package com.star.gforcemeter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private SensorManager mSensorManager;
    private TextView mAccelerationTextView;
    private TextView mMaxAccelerationTextView;
    private float currentAcceleration = 0;
    private float maxAcceleration = 0;

    private final double CALIBRATION = SensorManager.STANDARD_GRAVITY;

    private final SensorEventListener2 mSensorEventListener2 = new SensorEventListener2() {
        @Override
        public void onFlushCompleted(Sensor sensor) {

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            double a = Math.round(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2)));

            currentAcceleration = (float) Math.abs(a - CALIBRATION);

            if (currentAcceleration > maxAcceleration) {
                maxAcceleration = currentAcceleration;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccelerationTextView = (TextView) findViewById(R.id.acceleration);
        mMaxAccelerationTextView = (TextView) findViewById(R.id.maxAcceleration);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Timer updateTimer = new Timer("gForceUpdate");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGUI();
            }
        }, 0, 100);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorEventListener2, accelerometer,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorEventListener2);
        super.onPause();
    }

    private void updateGUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String currentG = currentAcceleration / SensorManager.STANDARD_GRAVITY + "Gs";

                mAccelerationTextView.setText(currentG);

                mAccelerationTextView.invalidate();

                String maxG = maxAcceleration / SensorManager.STANDARD_GRAVITY + "Gs";

                mMaxAccelerationTextView.setText(maxG);

                mMaxAccelerationTextView.invalidate();
            }
        });
    }
}
