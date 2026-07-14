package streamflow.servicio;

import java.util.List;
import streamflow.modelo.Contenido;
import streamflow.modelo.Usuario;

public interface IRecomendacionService {

    List<Contenido> recomendarPorGenero(Usuario u);
}
