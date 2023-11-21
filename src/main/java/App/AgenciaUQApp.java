package App;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AgenciaUQApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Notificar al servidor que la aplicación se está conectando
        notificarConexionServidor();

        FXMLLoader loader = new FXMLLoader(AgenciaUQApp.class.getResource("/Interfaces/Principal.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("Viajes UQ");
        stage.show();
    }

    public static void main(String[] args) {
        launch(AgenciaUQApp.class,args);
    }

    private void notificarConexionServidor() {
        // Puedes ajustar la dirección y el puerto según tu configuración
        String servidorDireccion = "localhost";
        int servidorPuerto = 12345;

        try (Socket socket = new Socket(servidorDireccion, servidorPuerto)) {
            // Enviar notificación de que AgenciaUQ se está conectando
            enviarMensaje(socket, "AgenciaUQ iniciada");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void enviarMensaje(Socket socket, String mensaje) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
            output.writeObject(mensaje);
            output.flush();
        }
    }

}