package streamflow.modelo;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private String id;
    private String nombre;
    private String email;
    private Suscripcion suscripcion;
    private final List<Contenido> favoritos = new ArrayList<>();

    public Usuario(String id, String nombre, String email, Suscripcion suscripcion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.suscripcion = suscripcion;
    }

    public void agregarFavorito(Contenido c) {
        if (c != null && !favoritos.contains(c)) {
            favoritos.add(c);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Suscripcion getSuscripcion() {
        return suscripcion;
    }

    public void setSuscripcion(Suscripcion suscripcion) {
        this.suscripcion = suscripcion;
    }

    public List<Contenido> getFavoritos() {
        return favoritos;
    }
}
