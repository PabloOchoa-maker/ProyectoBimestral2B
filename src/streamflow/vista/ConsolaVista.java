package streamflow.vista;

import java.util.List;
import java.util.Scanner;
import streamflow.controlador.ContenidoControlador;
import streamflow.controlador.UsuarioControlador;
import streamflow.modelo.Contenido;

/**
 * Implementacion de {@link IVista} basada en la consola de texto.
 */
public class ConsolaVista implements IVista {

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
    public void iniciar(ContenidoControlador contenidoControlador,
                        UsuarioControlador usuarioControlador) {
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
