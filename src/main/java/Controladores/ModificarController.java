package Controladores;

import Exceptions.ErrorGuardarCambios;
import Modelo.Cliente;
import Utils.ArchivoUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import lombok.extern.java.Log;

import java.net.URL;
import java.util.ResourceBundle;
@Log
public class ModificarController implements Initializable {

    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtContraseña;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtDocumento;

    private Cliente cliente;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtNombre.setText(cliente.getNombreCompleto());
        txtDocumento.setText(cliente.getCedula());
        txtContraseña.setText(cliente.getContraseña());
        txtCorreo.setText(cliente.getCorreo());
        txtDireccion.setText(cliente.getDireccion());
        txtTelefono.setText(cliente.getTelefono());
    }

    @FXML
    private void mostrarActualizar(ActionEvent event)  {
        try {
            String nuevoNombre = txtNombre.getText();
            String nuevoCorreo = txtCorreo.getText();
            String nuevoContraseña = txtContraseña.getText();
            String nuevoDireccion = txtDireccion.getText();
            String nuevoTelefono = txtTelefono.getText();

            if (!cliente.getCedula().equals(obtenerIdentificacionDelUsuario())) {
                ArchivoUtils.mostrarMensaje("Error", "Identificación incorrecta", "La identificación del cliente no coincide.", Alert.AlertType.ERROR);
                throw new ErrorGuardarCambios("La identificación del cliente no coincide.");
            }

            cliente.setNombreCompleto(nuevoNombre);
            cliente.setCorreo(nuevoCorreo);
            cliente.setTelefono(nuevoTelefono);
            cliente.setContraseña(nuevoContraseña);
            cliente.setDireccion(nuevoDireccion);
            ArchivoUtils.mostrarMensaje("Éxito", "Modificación exitosa", "Los cambios se guardaron correctamente.", Alert.AlertType.INFORMATION);

            log.severe("Se ha actualizado correctamente los datos de " + txtDocumento);

        } catch (Exception e) {
            ArchivoUtils.mostrarMensaje("Error", "Error al guardar cambios", "Hubo un error al intentar guardar los cambios.", Alert.AlertType.ERROR);
            e.printStackTrace(); // O manejo de errores específico
        }
    }

    private String obtenerIdentificacionDelUsuario() {
        return txtDocumento.getText();
    }

}