package streamflow.servicio;

import streamflow.modelo.Usuario;

public interface IUsuarioService {

    boolean registrar(Usuario u);

    Usuario buscarPorId(String id);
}
