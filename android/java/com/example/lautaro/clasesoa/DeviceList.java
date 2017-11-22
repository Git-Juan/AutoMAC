package com.example.lautaro.clasesoa;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//clase que lista los bluethoot vinculados al telefono
public class DeviceList extends AppCompatActivity {


    private ListView listViewConectores;
    private List<Conector> listaDeConectores;
    private AdapterConector adapterConector;
    private BluetoothAdapter mBtAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        listaDeConectores = new ArrayList<>();

        for (BluetoothDevice device : mBtAdapter.getBondedDevices()) {
            try {
                Conector con = new Conector(device.getName(), device.getAddress(), device.getUuids()[0].toString());
                listaDeConectores.add(con);
            } catch (Exception ex) {

            }

            //cargo en la lista el nombre de dispositivos vinculados y sus direcciones mac
            //mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        }

        listViewConectores = (ListView) findViewById(R.id.listDevices);
        adapterConector = new AdapterConector(listaDeConectores);
        listViewConectores.setAdapter(adapterConector);
        listViewConectores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Conector contacto = (Conector) listViewConectores.getItemAtPosition(position);

                SocketDevice.getInstance(contacto.getDireccion(), UUID.fromString(contacto.getUUID()));
                Intent appInfo = new Intent(DeviceList.this, ManejarActivity.class);

                //appInfo.putExtra("direccion", contacto.getDireccion());
                //appInfo.putExtra("uuid", UUID.fromString(contacto.getUUID()));
                startActivity(appInfo);

            }
        });
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

}


