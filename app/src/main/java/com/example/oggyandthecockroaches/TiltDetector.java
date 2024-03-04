package com.example.oggyandthecockroaches;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class TiltDetector {
    private Sensor sensor;
    private SensorManager sensorManager;
    private long timeStamp = 0;
    private CallBack_moves callBack_moves;

    public interface CallBack_moves {
        void moveR(); //right
        void moveL(); //left
    }

    public TiltDetector(Context con, CallBack_moves callB_steps) {
        this.callBack_moves = callB_steps;
        sensorManager = (SensorManager) con.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void start() {
        if (sensor != null) {
            sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // Handle the case where the accelerometer sensor is not available on the device
        }
    }

    public void stop() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void calcMove(float x) {
        float temp = System.currentTimeMillis() - timeStamp;
        if (temp > 300) {
            timeStamp = System.currentTimeMillis();
            if (x > 3.0 && callBack_moves != null)
                callBack_moves.moveL();
            else if (x < -3.0 && callBack_moves != null)
                callBack_moves.moveR();
        }
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            calcMove(event.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}