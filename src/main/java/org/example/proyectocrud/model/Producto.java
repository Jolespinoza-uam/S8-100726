package org.example.proyectocrud.model;

import java.time.LocalDate;

public class Producto {
    private int idProducto;
    private String codigo;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private LocalDate fechaCreacion;

    private int idCategoria;
    private String categoria;

    private int idMarca;
    private String marca;

    private int estado;

    public Producto(int idProducto, String codigo, String nombre, String descripcion, double precio, int stock, LocalDate fechaCreacion, int idCategoria, String categoria, int idMarca, String marca, int estado) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.fechaCreacion = fechaCreacion;
        this.idCategoria = idCategoria;
        this.categoria = categoria;
        this.idMarca = idMarca;
        this.marca = marca;
        this.estado = estado;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getIdMarca() {
        return idMarca;
    }

    public String getMarca() {
        return marca;
    }

    public int getEstado() {
        return estado;
    }
}
