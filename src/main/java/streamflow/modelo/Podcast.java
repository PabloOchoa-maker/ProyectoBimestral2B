package streamflow.modelo;

public class Podcast extends Contenido {

    private String presentador;

    public Podcast(String id, String titulo, Genero genero, Calidad calidad,
                   int duracionMin, double costoBase, String presentador) {
        super(id, titulo, genero, calidad, duracionMin, costoBase);
        this.presentador = presentador;
    }

    @Override
    public void reproducir() {
        System.out.println("Reproduciendo el podcast: " + titulo + " (" + calidad + ")");
    }

    @Override
    public String obtenerDetalles() {
        return "Podcast: " + titulo
                + " | Presentador: " + presentador
                + " | Genero: " + genero
                + " | Duracion: " + duracionMin + " min"
                + " | Calidad: " + calidad
                + " | Costo: " + calcularCosto();
    }

    public String getPresentador() {
        return presentador;
    }

    public void setPresentador(String presentador) {
        this.presentador = presentador;
    }
}
