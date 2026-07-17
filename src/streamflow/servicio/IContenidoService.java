package streamflow.servicio;

import java.util.List;
import streamflow.modelo.Contenido;

public interface IContenidoService {

    boolean registrar(Contenido c);

    List<Contenido> listar();
}
