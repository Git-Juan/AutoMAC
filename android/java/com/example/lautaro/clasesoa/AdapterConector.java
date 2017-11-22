package com.example.lautaro.clasesoa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lautaro on 04/10/17.
 */

public class AdapterConector  extends BaseAdapter {

    private List<Conector> conectores;

    public AdapterConector(List<Conector> contactos) {
        this.conectores = contactos;
    }

    //Devuelve Cantidad de items de la lista
    @Override
    public int getCount() {
        return conectores.size();
    }

    @Override
    public Object getItem(int position) {
        return conectores.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.celda_adapter,parent,false);
        Conector contacto = (Conector) getItem(position);

        TextView nombre = (TextView) view.findViewById(R.id.nombre);
        TextView direccion = (TextView) view.findViewById(R.id.direccion);
        TextView uuid = (TextView) view.findViewById(R.id.uuid);

        nombre.setText(String.valueOf(contacto.getNombre()));
        direccion.setText(String.valueOf(contacto.getDireccion()));
        uuid.setText(String.valueOf(contacto.getUUID()));

        return view;
    }
}
