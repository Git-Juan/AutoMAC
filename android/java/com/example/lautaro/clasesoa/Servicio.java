package com.example.lautaro.clasesoa;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by lautaro on 20/11/17.
 */

public class Servicio extends IntentService {

        private SocketDevice s;

    public Servicio() {
        super("AutomacService");
    }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {

        s = SocketDevice.getInstance();
        String comandoArduino  = intent.getStringExtra("comando");
        if(s.isAvailable())
            s.enviar(comandoArduino);
    }
}
