package Controladores;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class GestionController implements Initializable {
    public Button destinosBoton;
    public Button estadisticasBoton;
    public Button paquetesBoton;
    public Button guiasBoton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void mostrarDestinos() {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Interfaces/Destinos.fxml")));
            PrincipalController.panelFormulario.getChildren().setAll(node);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void mostrarEstadisticas() {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Interfaces/Estadisticas.fxml")));
            PrincipalController.panelFormulario.getChildren().setAll(node);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void mostrarPaquetes() {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Interfaces/Paquetes.fxml")));
            PrincipalController.panelFormulario.getChildren().setAll(node);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void mostrarGuias() {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Interfaces/Guias.fxml")));
            PrincipalController.panelFormulario.getChildren().setAll(node);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}