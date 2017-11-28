package com.example.lautaro.clasesoa;

/**
 * Created by juan on 28/11/17.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class Servicio extends Service{

    private SocketDevice s;


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        s = SocketDevice.getInstance();
        String comandoArduino  = intent.getStringExtra("comando");
        if(s.isAvailable()) {
            s.enviar(comandoArduino);
        }
        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        /*  */
        super.onDestroy();
        // Log.d(TAG, "FirstService destroyed");
    }

}
