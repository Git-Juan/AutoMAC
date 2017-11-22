package com.example.lautaro.clasesoa;

/**
 * Created by lautaro on 04/10/17.
 */

public class Conector {

    private String  direccion;
    private String  nombre;
    private String  UUID;

    public Conector(String nombre,String direccion,String uuid){
        this.nombre = nombre;
        this.direccion = direccion;
        this.UUID = uuid;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
}
