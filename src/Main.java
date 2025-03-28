/**
 * Clase Main
 */

import com.Modelo.Modelo;
import com.Vista.Vista;
import com.Controlador.Controlador;
import javax.swing.*;

public class Main {
    /**
     * Meteodo Main encargado de Verificar el Look a feel y crear las instancias de modelo, vista controlador
     * @param args
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error al configurar el look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            Modelo modelo = new Modelo();
            Vista vista = new Vista();
            new Controlador(modelo, vista);

            System.out.println("Aplicación iniciada correctamente");
        });
    }
}