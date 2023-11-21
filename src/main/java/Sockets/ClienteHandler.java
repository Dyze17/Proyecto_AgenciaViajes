package Sockets;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class ClienteHandler implements Runnable {
    private Socket socket;
    private ServidorSocket servidor;
    private PrintWriter salida;
    private BufferedReader entrada;
    @Getter
    private String nombreUsuario;

    public ClienteHandler(Socket socket, ServidorSocket servidor) {
        this.socket = socket;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try {
            // Configura el flujo de entrada del cliente
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Solicita al cliente que se registre proporcionando su nombre
            salida = new PrintWriter(socket.getOutputStream(), true);
            salida.println("Bienvenido al servidor. Por favor, introduce tu nombre:");
            nombreUsuario = entrada.readLine();

            // Notifica al servidor que el cliente se ha registrado
            servidor.notificarRegistro(this);

            // Muestra un mensaje de bienvenida o realiza otras acciones según tus necesidades
            salida.println("¡Bienvenido, " + nombreUsuario + "!");
            // Lógica para recibir y enviar mensajes
            // Ejemplo: mientras el cliente esté conectado, lee mensajes y envía respuestas
            // Utiliza servidor.broadcastMensaje(mensaje, this) para enviar mensajes a todos los clientes

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Asegúrate de cerrar el socket y eliminar este ClienteHandler de la lista en ServidorSocket
            //servidor.eliminarCliente(this);
            try {
                socket.close();
            } catch (SocketException se) {
                se.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void enviarMensaje(String mensaje) {
        // Envía un mensaje al cliente
        salida.println(mensaje);
    }

    // Otros métodos y lógica aquí
}