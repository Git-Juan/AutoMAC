package com.example.lautaro.clasesoa;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class testSensor extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView x;
    private TextView y;
    private TextView z;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sensor);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        x = (TextView) findViewById(R.id.txtX);
        y = (TextView) findViewById(R.id.txtY);
        z = (TextView) findViewById(R.id.txtZ);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor , SensorManager.SENSOR_DELAY_NORMAL);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {

        //if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {


            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                x.setText( Float.toString(event.values[0]));
                y.setText(Float.toString(event.values[1]));
                z.setText(Float.toString(event.values[2]));
                //detectShake(event);
            }

            //detectRotation(event);


       // }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
