package Sockets;

import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServidorSocket {
    int puerto = 12345;
    private ServerSocket serverSocket;
    @Getter
    private static ArrayList<ClienteHandler> clientes;
    public ServidorSocket() {
        // Inicializa tus variables y crea el ServerSocket
        clientes = new ArrayList<ClienteHandler>();
    }

    public void iniciarServidor(int puerto) {
        try {
            serverSocket = new ServerSocket(puerto);
            System.out.println("Servidor esperando conexiones en el puerto " + puerto);

            while (true) {
                Socket socketCliente = serverSocket.accept();

                ClienteHandler clienteHandler = new ClienteHandler(socketCliente, this);

                // Almacena el ClienteHandler en la lista
                clientes.add(clienteHandler);

                // Inicia un nuevo hilo para manejar la conexión
                Thread hiloCliente = new Thread(clienteHandler);
                hiloCliente.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void broadcastMensaje(String mensaje, ClienteHandler remitente) {
        // Método para enviar mensajes a todos los clientes conectados
        for (ClienteHandler cliente : clientes) {
            if (cliente != remitente) {
                cliente.enviarMensaje(mensaje);
            }
        }
    }

    public synchronized void notificarRegistro(ClienteHandler cliente) {
        // Método para notificar al servidor que un cliente se ha registrado
        System.out.println(cliente.getNombreUsuario() + " se ha conectado.");
        broadcastMensaje("¡" + cliente.getNombreUsuario() + " se ha unido al chat!", null);
    }

    public synchronized void eliminarCliente(ClienteHandler clienteHandler) {
        clientes.remove(clienteHandler);
    }

    public static void main(String[] args) {
        // Puedes ajustar el puerto según tus necesidades
        ServidorSocket servidorSocket = new ServidorSocket();
        servidorSocket.iniciarServidor(12345);
    }

}