package com.example.lautaro.clasesoa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;



public class ManejarActivity extends AppCompatActivity implements SensorEventListener {

    private Button btnAcelerar;
    private Button btnReversa;
    private Button btnStop;
    private Switch switchPrendido;
    private Switch switchSensores;
    private SeekBar seekBarVelocidad;
    private TextView txtVelocidad;

    private SensorManager sensorManager;
    private Sensor sensor;
    private Sensor sensorLuz;
    private Sensor sensorProximidad;
    private SocketDevice s;
    private String velocidadActual;
    private boolean primeraVezAvelerar = false;
    private boolean primeraVezReversa = false;
    private boolean primeraVezLuz = false;
    private Intent intentService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manejar);

        try{
            intentService = new Intent(ManejarActivity.this, Servicio.class);

            //automaticamente la pantalla se pone en landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //Asociacion de componentes visuales
        btnAcelerar = (Button) findViewById(R.id.btnAvanzar);
        btnReversa = (Button) findViewById(R.id.btnFrenar);
        btnStop = (Button) findViewById(R.id.btnStop);

        switchPrendido = (Switch) findViewById(R.id.switchPrendido);
            switchSensores  = (Switch) findViewById(R.id.swtichSensores);
        txtVelocidad = (TextView) findViewById(R.id.txtVelocidad);
            txtVelocidad.setText("0,0");
        seekBarVelocidad = (SeekBar) findViewById(R.id.seekBarVelocidad);


        //registro de sensores con los que vamos a trabajar
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorProximidad = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorLuz, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,sensorProximidad, SensorManager.SENSOR_DELAY_NORMAL);

            //eventos de los botones acelear, frenar, barra de velocidades
            btnAcelerar.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    if(event.getAction() == MotionEvent.ACTION_DOWN){

                        if(!primeraVezAvelerar) {
                            intentService.putExtra("comando", velocidadActual);
                            startService(intentService);
                            intentService.getExtras().clear();
                            intentService.putExtra("comando", "a");
                            intentService.getExtras().clear();
                            startService(intentService);
                            primeraVezAvelerar = true;
                        }
                    }

                    if(event.getAction() == MotionEvent.ACTION_UP) {
                        primeraVezAvelerar = false;

                        intentService.putExtra("comando", "f");
                        startService(intentService);
                        intentService.getExtras().clear();
                    }
                    return false;
                }
            });




            //evento para ir de reversa
            btnReversa.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        if(!primeraVezReversa) {
                            intentService.putExtra("comando", "r");
                            startService(intentService);
                            intentService.getExtras().clear();


                            primeraVezReversa = true;
                        }
                    }

                    if(event.getAction() == MotionEvent.ACTION_UP){

                        intentService.putExtra("comando", "f");
                        startService(intentService);
                        intentService.getExtras().clear();

                        primeraVezReversa = false;
                    }
                    return false;
                }
            });

            btnStop.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    intentService.putExtra("comando", "f");
                    startService(intentService);
                    intentService.getExtras().clear();
                    return false;
                }
            });

            switchPrendido.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



                    intentService.getExtras().clear();


                    if (isChecked){


                        intentService.putExtra("comando", "p");
                        startService(intentService);
                    }else{

                        intentService.putExtra("comando", "o");
                        startService(intentService);
                    }


                }
            });

            seekBarVelocidad.setProgress(0);
        seekBarVelocidad.incrementProgressBy(1);
        seekBarVelocidad.setMax(4);
        velocidadActual = "1";

        seekBarVelocidad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int valor, boolean b) {
                velocidadActual = Integer.toString(valor + 1);
                if(valor == 0){
                    txtVelocidad.setText("1,1");
                }
                if(valor == 1){
                    txtVelocidad.setText("1,4");
                }
                if(valor == 2){
                    txtVelocidad.setText("1,8");
                }
                if(valor == 3){
                    txtVelocidad.setText("2,2");
                }
                if(valor == 4){
                    txtVelocidad.setText("2,5");
                }
                //Toast.makeText(getBaseContext(), Integer.toString(valor), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //

            }
        });

        s = SocketDevice.getInstance();
        s.open();
        if(s.isAvailable()){
            Toast.makeText(getBaseContext(), "Se conectó", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getBaseContext(), "No se conectó", Toast.LENGTH_LONG).show();
        }
        }catch (Exception ex){}
    }

    @Override
    public void onSensorChanged(SensorEvent event) {


        if(switchSensores.isChecked()) {

            //chequeo sensor de proximidad
            if(event.sensor.getType() == Sensor.TYPE_PROXIMITY){

                if(event.values[0] <= 4)
                {

                    intentService.putExtra("comando", "p");
                    startService(intentService);
                    intentService.getExtras().clear();
                    intentService.putExtra("comando", "1");
                    startService(intentService);
                    intentService.getExtras().clear();
                    intentService.putExtra("comando", "a");
                    startService(intentService);
                    intentService.getExtras().clear();


                }else{
                    intentService.putExtra("comando", "f");
                    startService(intentService);
                    intentService.getExtras().clear();


                }



                String valor = String.valueOf(event.values[0]);
                Log.i("Proximidad",valor);
            }
            //solo switch activo da LUZ
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {

                if (event.values[0] > 300) {
                    if(!primeraVezLuz) {

                        intentService.putExtra("comando", "z");
                        startService(intentService);
                        intentService.getExtras().clear();
                        primeraVezLuz = true;
                    }
                }else{
                    if(primeraVezLuz) {

                        intentService.putExtra("comando", "y");
                        startService(intentService);
                        intentService.getExtras().clear();
                        primeraVezLuz = false;
                    }
                }
            }
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            //min izquierda
            if(btnAcelerar.isPressed()) {
                if (event.values[1] < -4 && event.values[1] > -5) {
                    //doblar Izquierda

                    intentService.putExtra("comando", "c");
                }

                //med izquierda
                if (event.values[1] < -5 && event.values[1] > -7) {
                    //doblar Izquierda

                    intentService.putExtra("comando", "e");
                }
                //max izquierda
                if (event.values[1] < -7 && event.values[1] > -9) {
                    //doblar Izquierda

                    intentService.putExtra("comando", "h");
                }
                //min derecha
                if (event.values[1] > 4 && event.values[1] < 5) {
                    //doblar Derecha

                    intentService.putExtra("comando", "b");
                }
                //med derecha
                if (event.values[1] > 5 && event.values[1] < 7) {
                    //doblar Derecha

                    intentService.putExtra("comando", "d");
                }
                //max derecha
                if (event.values[1] > 7 && event.values[1] < 9) {
                    //doblar Derecha

                    intentService.putExtra("comando", "g");
                }


                startService(intentService);
                intentService.getExtras().clear();
            }

            if (btnReversa.isPressed()) {
                //min izquierda
                if (event.values[1] < 4 && event.values[1] > 5) {

                    intentService.putExtra("comando", "j");
                }
                //min derecha
                if (event.values[1] < -4 && event.values[1] > -5) {

                    intentService.putExtra("comando", "i");
                }
                //med izquierda
                if (event.values[1] > 5 && event.values[1] < 7) {

                    intentService.putExtra("comando", "l");
                }

                //med derecha
                if (event.values[1] < -5 && event.values[1] > -7) {

                    intentService.putExtra("comando", "k");
                }

                //max ixquierda
                if (event.values[1] > 7 && event.values[1] < 9) {

                    intentService.putExtra("comando", "n");
                }
                //max derecha
                if (event.values[1] < -7 && event.values[1] > -9) {

                    intentService.putExtra("comando", "m");
                }

                startService(intentService);
                intentService.getExtras().clear();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }





}




