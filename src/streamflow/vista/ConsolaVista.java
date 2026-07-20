package streamflow.vista;

import java.util.List;
import java.util.Scanner;
import streamflow.controlador.IContenidoControlador;
import streamflow.controlador.IUsuarioControlador;
import streamflow.modelo.Contenido;

/**
 * Implementacion de {@link IVista} basada en la consola de texto.
 */
public class ConsolaVista implements IVista {

    private final IContenidoControlador contenidoControlador;
    private final IUsuarioControlador usuarioControlador;

    /**
     * Cablea esta vista concreta con sus colaboradores (los controladores).
     * La dependencia hacia las interfaces de controlador vive aqui, en la
     * implementacion, no en el contrato {@link IVista}.
     *
     * @param contenidoControlador controlador para operaciones de contenido
     * @param usuarioControlador    controlador para operaciones de usuario
     */
    public ConsolaVista(IContenidoControlador contenidoControlador,
                        IUsuarioControlador usuarioControlador) {
        this.contenidoControlador = contenidoControlador;
        this.usuarioControlador = usuarioControlador;
    }

    @Override
    public void mostrarMenu() {
        System.out.println();
        System.out.println("========= STREAMFLOW =========");
        System.out.println("1. Listar contenidos");
        System.out.println("2. Facturar usuario");
        System.out.println("3. Recomendar por genero");
        System.out.println("0. Salir");
        System.out.println("==============================");
        System.out.print("Seleccione una opcion: ");
    }

    @Override
    public void mostrarContenidos(List<Contenido> lista) {
        if (lista == null || lista.isEmpty()) {
            System.out.println("No hay contenidos para mostrar.");
            return;
        }
        System.out.println("--- Contenidos disponibles (" + lista.size() + ") ---");
        for (Contenido c : lista) {
            System.out.println(" - " + c.obtenerDetalles());
        }
    }

    @Override
    public void iniciar() {
        Scanner sc = new Scanner(System.in);
        String opcion;
        do {
            mostrarMenu();
            opcion = sc.nextLine().trim();
            switch (opcion) {
                case "1":
                    mostrarContenidos(contenidoControlador.listar());
                    break;
                case "2":
                    System.out.print("ID del usuario a facturar: ");
                    String idFactura = sc.nextLine().trim();
                    double total = usuarioControlador.facturar(idFactura);
                    mostrarMensaje("Costo mensual total: $" + total);
                    break;
                case "3":
                    System.out.print("ID del usuario para recomendar: ");
                    String idRec = sc.nextLine().trim();
                    List<Contenido> recomendados = usuarioControlador.recomendar(idRec);
                    mostrarContenidos(recomendados);
                    break;
                case "0":
                    mostrarMensaje("Hasta pronto.");
                    break;
                default:
                    mostrarMensaje("Opcion no valida.");
            }
        } while (!opcion.equals("0"));
        sc.close();
    }

    /**
     * Muestra un mensaje generico en la consola.
     *
     * @param mensaje texto a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }
}
