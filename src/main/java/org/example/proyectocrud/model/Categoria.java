package org.example.proyectocrud.model;

public class Categoria {
    private int idCategoria;
    private String nombre;
    //private String descripcion;
    //private int estado;

    public Categoria(int idCategoria, String nombre) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNombre() {
        return nombre;
    }
}
