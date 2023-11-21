package Controladores;

import Exceptions.AtributoVacioException;
import Modelo.AgenciaUQ;
import Utils.ArchivoUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import Enum.EstadoPQRS;

public class PqrsController implements Initializable {
    @FXML
    private ChoiceBox<EstadoPQRS> boxPQRS;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextArea txtAreaPQRS;
    @FXML
    private Button btnRealizarPQRS;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        boxPQRS.getItems().addAll(EstadoPQRS.values());
    }

    @FXML
    void showPQRS(ActionEvent event) throws AtributoVacioException, IOException {
        String nombre = txtNombre.getText();
        EstadoPQRS estadoPQRS = boxPQRS.getValue();
        String mensaje = txtAreaPQRS.getText();

        try {
            AgenciaUQ.getInstance().crearPqrs(nombre, estadoPQRS, mensaje);
            ArchivoUtils.mostrarMensaje("Éxito", "PQRS realizado exitosamenta", "El PQRS se ha creado correctamente.", Alert.AlertType.INFORMATION);
        } catch(AtributoVacioException | IOException e){
            ArchivoUtils.mostrarMensaje("Error", "Error al realizar PQRS", "Hubo un error al intentar crear una PQRS.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}


