package Controladores;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class RegistrarController implements Initializable {

    public TextField claveTxt;
    public TextField nombreTxt;
    public TextField DocTxt;
    public TextField emailTxt;
    public TextField celTxt;
    public TextField direccionTxt;
    public Button registrarBoton;
    public ComboBox<String> admCombo;
    private final String[] rol = {"Administrador", "Cliente","Guía"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Agregar elementos al ComboBox durante la inicialización
        admCombo.getItems().addAll(rol);

        // Configurar el campo de texto nombreTxt para permitir solo letras
        nombreTxt.setTextFormatter(new TextFormatter<>((TextFormatter.Change change) -> {
            if (change.getText().matches("[a-zA-Z\\s]*")) {
                return change;
            }
            return null;
        }));

        // Configurar los campos de texto DocTxt y celTxt para permitir solo números
        DocTxt.setTextFormatter(new TextFormatter<>((TextFormatter.Change change) -> {
            if (change.getText().matches("\\d*")) {
                return change;
            }
            return null;
        }));

        celTxt.setTextFormatter(new TextFormatter<>((TextFormatter.Change change) -> {
            if (change.getText().matches("\\d*")) {
                return change;
            }
            return null;
        }));
    }

    public void registrarDatos() {
        // Obtener los datos de los TextField
            String clave = claveTxt.getText();
            String nombre = nombreTxt.getText();
            String documento = DocTxt.getText();
            String email = emailTxt.getText();
            String celular = celTxt.getText();
            String direccion = direccionTxt.getText();

        // Validar que todos los campos estén llenos antes de guardar
        if (clave.isEmpty() || nombre.isEmpty() || documento.isEmpty() || email.isEmpty() || celular.isEmpty() || direccion.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos.");
            return;
        }

        // Obtener el rol seleccionado del ComboBox
        String rolSeleccionado = admCombo.getSelectionModel().getSelectedItem();

        // Validar que se haya seleccionado un rol
        try {
            if (rolSeleccionado.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor, seleccione un rol.");
                return;
            }
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione un rol.");
            return;
        }
        if (encontrarDocumentoExistente(documento)) {
            JOptionPane.showMessageDialog(null, "El documento ya existe");
            return;
        }

        // Crear una cadena con los datos a guardar en el archivo, incluyendo el rol
        String datosAGuardar = String.format("%s,%s,%s,%s,%s,%s,%s\n", clave, nombre, documento, email, celular, direccion, rolSeleccionado);

        // Especificar la ruta del archivo donde se guardarán los datos
        String rutaArchivo = "src/main/resources/Data/users.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo, true))) {
            // Escribir los datos en el archivo
            writer.write(datosAGuardar);
            JOptionPane.showMessageDialog(null, "Usuario registrado con éxito.");
            claveTxt.setText("");
            nombreTxt.setText("");
            DocTxt.setText("");
            emailTxt.setText("");
            celTxt.setText("");
            direccionTxt.setText("");
            admCombo.getSelectionModel().clearSelection();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar usuario." + e.getMessage());
        }
    }
    private boolean encontrarDocumentoExistente(String documento) {
        try (Scanner scanner = new Scanner(new File("src/main/resources/Data/users.txt"))) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] partes = linea.split(",");
                if (partes.length > 2 && partes[2].trim().equals(documento.trim())) {
                    return true; // El documento ya existe en el archivo
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(); // Manejar la excepción adecuadamente según tus necesidades
        }
        return false; // El documento no existe en el archivo
    }
}

