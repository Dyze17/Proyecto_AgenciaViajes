package Controladores;

import Modelo.AgenciaUQ;
import Modelo.Destino;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PaquetesController implements Initializable {
    private final PrincipalController principalController = PrincipalController.getInstance();
    private final AgenciaUQ agenciaUQ = AgenciaUQ.getInstance();
    public Button añadirBoton;
    public Button gestionarBoton;
    public TextField nombreField;
    public ComboBox<Destino> destinosBox;
    public TextArea descripcionArea;
    public DatePicker fechaInicial;
    public DatePicker fechaFinal;
    public Button añadirPaqueteBoton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void mostrarAñadir() {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Interfaces/AñadirPaquete.fxml")));
            principalController.panelFormulario.getChildren().setAll(node);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}