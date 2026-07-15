package streamflow.vista;

import java.util.List;
import streamflow.modelo.Contenido;

public interface IVista {

    void mostrarMenu();

    void mostrarContenidos(List<Contenido> lista);
}
