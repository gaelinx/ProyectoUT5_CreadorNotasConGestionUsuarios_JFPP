
import com.Modelo.Modelo;
import com.Vista.Vista;
import com.Controlador.Controlador;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Establecer el aspecto visual del sistema operativo
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error al configurar el look and feel: " + e.getMessage());
        }

        // Ejecutar en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            // Inicializar los componentes MVC
            Modelo modelo = new Modelo();
            Vista vista = new Vista();
            new Controlador(modelo, vista);

            System.out.println("Aplicación iniciada correctamente");
        });
    }
}