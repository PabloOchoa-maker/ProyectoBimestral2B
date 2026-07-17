package streamflow.modelo;

import streamflow.modelo.contrato.Detallable;
import streamflow.modelo.contrato.Reproducible;

public abstract class Contenido implements Detallable, Reproducible {

    protected String id;
    protected String titulo;
    protected Genero genero;
    protected Calidad calidad;
    protected int duracionMin;
    protected double costoBase;

    protected Contenido(String id, String titulo, Genero genero, Calidad calidad,
                        int duracionMin, double costoBase) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.calidad = calidad;
        this.duracionMin = duracionMin;
        this.costoBase = costoBase;
    }

    public double calcularCosto() {
        return costoBase * calidad.getFactor();
    }

    @Override
    public abstract void reproducir();

    @Override
    public abstract String obtenerDetalles();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public Calidad getCalidad() {
        return calidad;
    }

    public void setCalidad(Calidad calidad) {
        this.calidad = calidad;
    }

    public int getDuracionMin() {
        return duracionMin;
    }

    public void setDuracionMin(int duracionMin) {
        this.duracionMin = duracionMin;
    }

    public double getCostoBase() {
        return costoBase;
    }

    public void setCostoBase(double costoBase) {
        this.costoBase = costoBase;
    }
}
