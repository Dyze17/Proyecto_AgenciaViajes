package Controladores;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Objects;

public class IniciarSesionController {
    private static IniciarSesionController instance;
    public Button RegistrarBoton;
    public TextField documentoTxt;
    public TextField claveTxt;
    public Button iniciarBoton;
    static boolean iniciado;
    static boolean administrador;

    public static IniciarSesionController getInstance() {
        if (instance == null) {
            instance = new IniciarSesionController();
        }
        return instance;
    }
    public void mostrarRegistrar() {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Interfaces/Registrar.fxml")));
            PrincipalController.getInstance().panelFormulario.getChildren().setAll(node);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void iniciarSesion() {
        iniciado = false;
        String documento = documentoTxt.getText().trim(); // Utilizamos trim() para eliminar espacios en blanco
        String clave = claveTxt.getText().trim();

        if (documento.isEmpty() || clave.isEmpty()) {
            mostrarMensaje("Error: Por favor, complete todos los campos.");
            return;
        }

        // Verificar las credenciales en el archivo txt
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/Data/users.txt"))) {
            String line;
            boolean credencialesCorrectas = false; // Variable para verificar si se encontraron credenciales válidas

            while ((line = br.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length >= 3 && userData[2].equals(documento) && userData[0].equals(clave)) {
                    credencialesCorrectas = true;
                    administrador = userData[6].equalsIgnoreCase("administrador");
                    break; // Salimos del bucle si encontramos credenciales válidas
                }
            }

            // Mostrar mensaje según el resultado de la verificación
            if (credencialesCorrectas) {
                mostrarMensaje("Inicio de sesión exitoso");
                claveTxt.setText("");
                documentoTxt.setText("");
                iniciado = true;
                try {
                    Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Interfaces/SesionIniciada.fxml")));
                    PrincipalController.getInstance().panelFormulario.getChildren().setAll(node);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            } else {
                mostrarMensaje("Por favor, Verifique su documento y contraseña.");
            }

        } catch (Exception e) {
            mostrarMensaje("Error al leer el archivo: " + e.getMessage());
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
