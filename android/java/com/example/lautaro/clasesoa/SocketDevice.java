package com.example.lautaro.clasesoa;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by lautaro on 16/10/17.
 */

//Esta clase es la que hace la conexion al arduino via el socket bluethoot
public class SocketDevice {

    private static SocketDevice INSTANCE = null;
    private static BluetoothSocket socket;
    private static BluetoothAdapter mBtAdapter;


    //Private constructor prevents instantiating and subclassing
    private SocketDevice(){


        // instanciates the socket ...
    }

    //Static 'instance' method
    public static SocketDevice getInstance(String address,UUID BTMODULEUUID ) {

            try{
                if(INSTANCE == null) {
                    INSTANCE =  new SocketDevice();
                    mBtAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
                    socket = device.createRfcommSocketToServiceRecord(BTMODULEUUID);

                }

            }catch (IOException ex){

            }
            return INSTANCE;
    }


    public static SocketDevice getInstance(){
        return INSTANCE;
    }
    public void open(){
        try{
            if(!isAvailable())
                socket.connect();
        }catch (IOException ex){
            ex.getMessage();
        }

    }

    public void close(){
        try{
            socket.close();
        }catch (IOException ex){

        }
    }

    public void enviar(String comando){
        try{
            //String message = "chauuuu";
            byte[] mensaje = comando.getBytes();
            String mensj = new String(mensaje, Charset.defaultCharset());

            socket.getOutputStream().write(mensj.getBytes());
        }catch (IOException ex){
            String mensaje = ex.getMessage();
        }

    }

    public InputStream recibir(){
       try {
           return socket.getInputStream();

       }catch (IOException ex){
           return null;
       }
    }


    public boolean isAvailable(){
        return socket.isConnected();
    }
}
