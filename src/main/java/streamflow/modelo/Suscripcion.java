package streamflow.modelo;

import java.time.LocalDate;

public class Suscripcion {

    private String id;
    private LocalDate fechaInicio;
    private double costoMensual;

    public Suscripcion(String id, LocalDate fechaInicio, double costoMensual) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.costoMensual = costoMensual;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public double getCostoMensual() {
        return costoMensual;
    }

    public void setCostoMensual(double costoMensual) {
        this.costoMensual = costoMensual;
    }
}
