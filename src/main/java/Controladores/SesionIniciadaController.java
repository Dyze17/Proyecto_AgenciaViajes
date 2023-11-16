package Controladores;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SesionIniciadaController {
    IniciarSesionController iniciar = IniciarSesionController.getInstance();
    public Label name;
    public Button salirBoton;
    public void mostrarInicio() {
        try {
            Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Interfaces/IniciarSesion.fxml")));
            PrincipalController.getInstance().panelFormulario.getChildren().setAll(node);
            iniciar.iniciado = false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
