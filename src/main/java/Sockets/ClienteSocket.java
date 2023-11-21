package Sockets;

import Modelo.AgenciaUQ;
import Modelo.Cliente;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClienteSocket {
    private AgenciaUQ agenciaUQ;
    public ClienteSocket(AgenciaUQ agenciaUQ) {
        this.agenciaUQ = agenciaUQ;
    }

    public static void main(String[] args) {
        String servidorDireccion = "localhost";
        int servidorPuerto = 12345;

        try (Socket socket = new Socket(servidorDireccion, servidorPuerto)) {
            System.out.println("Conectado al servidor");

            // Enviar notificación de que AgenciaUQ se ha iniciado
            enviarMensaje(socket, "AgenciaUQ iniciada");

            // Aquí puedes implementar la lógica para enviar mensajes cuando un usuario se registra

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

    public void notificarClienteConectado(String nombreUsuario) {
        agenciaUQ.notificarClienteConectado(nombreUsuario);
    }


}
