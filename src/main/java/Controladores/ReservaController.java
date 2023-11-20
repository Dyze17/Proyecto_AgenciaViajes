package Controladores;

import Utils.ArchivoUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ReservaController implements Initializable {

    @FXML
    private Button btnAgregar;
    @FXML
    private Button btnModificar;
    public PrincipalController principalController = PrincipalController.getInstance();

    @FXML
    void showAgregar(ActionEvent event) {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Interfaces/ReservaAgregar.fxml")));
            principalController.panelFormulario.getChildren().setAll(node);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void showModificar(ActionEvent event) {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Interfaces/ReservaModificar.fxml")));
            principalController.panelFormulario.getChildren().setAll(node);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
