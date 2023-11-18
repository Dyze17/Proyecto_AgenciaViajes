package Controladores;

import Exceptions.ErrorGuardarCambios;
import Modelo.AgenciaUQ;
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
    }

    @FXML
    private void mostrarActualizar(ActionEvent event) throws ErrorGuardarCambios {
        try {
            String nuevoNombre = txtNombre.getText();
            String nuevoDocumento = txtDocumento.getText();
            String nuevoCorreo = txtCorreo.getText();
            String nuevoContraseña = txtContraseña.getText();
            String nuevoDireccion = txtDireccion.getText();
            String nuevoTelefono = txtTelefono.getText();

            if (!AgenciaUQ.getAgenciaUQ().obtenerClienteCedula(nuevoDocumento)) {
                ArchivoUtils.mostrarMensaje("Error", "Documento no encontrado", "El documento ingresado no está en la lista.", Alert.AlertType.ERROR);
                throw new ErrorGuardarCambios("El documento del cliente no está en la lista.");
            }

            AgenciaUQ.getInstance().modificarUsuario(nuevoDocumento, nuevoNombre, nuevoCorreo, nuevoContraseña, nuevoDireccion, nuevoTelefono);
            ArchivoUtils.mostrarMensaje("Éxito", "Modificación exitosa", "Los cambios se guardaron correctamente.", Alert.AlertType.INFORMATION);
            log.severe("Se ha actualizado correctamente los datos de " + txtDocumento);

        } catch (Exception e) {
            ArchivoUtils.mostrarMensaje("Error", "Error al guardar cambios", "Hubo un error al intentar guardar los cambios.", Alert.AlertType.ERROR);
            throw new ErrorGuardarCambios("No se encontró ningún cliente con la cédula proporcionada.");
        }
    }



}