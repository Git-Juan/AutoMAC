package com.example.lautaro.clasesoa;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ListView listViewConectores;
    private List<Conector> listaDeConectores;
    private AdapterConector adapterConector;
    private BluetoothAdapter mBtAdapter;
    private UUID BTMODULEUUID;
    private BluetoothSocket btSocket = null;
    private String address;
    private OutputStream outputStream;
    private InputStream inStream;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    private void checkBTState() {
        // Check device has Bluetooth and that it is turned on
        mBtAdapter=BluetoothAdapter.getDefaultAdapter(); // CHECK THIS OUT THAT IT WORKS!!!
        if(mBtAdapter==null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d("TAG", "...Bluetooth Activado...");
            } else {
                //Prompt user to turn on Bluetooth
                //Con esto hago que pregunte
                //Puedo activar el Bluetooth ?
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
        protected void onResume() {
            super.onResume();

            btnEnviar = (Button) findViewById(R.id.enviar);
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            checkBTState();

            listaDeConectores =  new ArrayList<>();

            for (BluetoothDevice device : mBtAdapter.getBondedDevices()) {
                try{
                    Conector con = new Conector(device.getName(),device.getAddress(),device.getUuids()[0].toString());
                    listaDeConectores.add(con);
                }catch (Exception ex){

                }

                //cargo en la lista el nombre de dispositivos vinculados y sus direcciones mac
                //mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }

            listViewConectores =  (ListView) findViewById(R.id.listView);
            adapterConector =  new AdapterConector(listaDeConectores);
            listViewConectores.setAdapter(adapterConector);


        listViewConectores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Conector contacto = (Conector) listViewConectores.getItemAtPosition(position);

                BTMODULEUUID = UUID.fromString(contacto.getUUID());
                address = contacto.getDireccion();
                ConexionBlue();

                btnEnviar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            String message = "chauuuu";
                            byte[] mensaje = message.getBytes();
                            outputStream = btSocket.getOutputStream();
                            inStream = btSocket.getInputStream();
                            outputStream.write(mensaje);
                        } catch (IOException e) {
                            Log.d(
                                    "ERROR", "Bug while sending stuff", e);
                        }
                    }
                });
            }
        });




        }

    public void ConexionBlue() {

        BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();

            Toast.makeText(getBaseContext(), "Se conectoooooooooooooo", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            try {
                btSocket.close();
                Toast.makeText(getBaseContext(), "No se puede conectar", Toast.LENGTH_LONG).show();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }

        try{



        }catch (Exception ex){

        }




    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
