package streamflow.modelo;

public class Documental extends Contenido {

    private String tema;

    public Documental(String id, String titulo, Genero genero, Calidad calidad,
                      int duracionMin, double costoBase, String tema) {
        super(id, titulo, genero, calidad, duracionMin, costoBase);
        this.tema = tema;
    }

    @Override
    public void reproducir() {
        System.out.println("Reproduciendo el documental: " + titulo + " (" + calidad + ")");
    }

    @Override
    public String obtenerDetalles() {
        return "Documental: " + titulo
                + " | Tema: " + tema
                + " | Genero: " + genero
                + " | Duracion: " + duracionMin + " min"
                + " | Calidad: " + calidad
                + " | Costo: " + calcularCosto();
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }
}
