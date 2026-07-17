package streamflow.modelo;

public class Pelicula extends Contenido {

    private String director;

    public Pelicula(String id, String titulo, Genero genero, Calidad calidad,
                    int duracionMin, double costoBase, String director) {
        super(id, titulo, genero, calidad, duracionMin, costoBase);
        this.director = director;
    }

    @Override
    public void reproducir() {
        System.out.println("Reproduciendo la pelicula: " + titulo + " (" + calidad + ")");
    }

    @Override
    public String obtenerDetalles() {
        return "Pelicula: " + titulo
                + " | Director: " + director
                + " | Genero: " + genero
                + " | Duracion: " + duracionMin + " min"
                + " | Calidad: " + calidad
                + " | Costo: " + calcularCosto();
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }
}
