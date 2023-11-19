package Modelo;

import Controladores.IniciarSesionController;
import Exceptions.AtributoVacioException;
import Exceptions.CupoInvalidoException;
import Exceptions.ErrorGuardarCambios;
import Exceptions.FechaNoValidaException;
import Utils.ArchivoUtils;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.extern.java.Log;
import java.util.Optional;
import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

import Enum.EstadoReserva;
@Log
public class AgenciaUQ {
    @Getter
    private static AgenciaUQ agenciaUQ;
    @Getter
    private final ArrayList<Destino> destinos;
    @Getter
    private ArrayList<Reserva> reservas;
    @Getter
    private final ArrayList<PaqueteTuristico> paquetes;
    @Getter
    private final ArrayList<Cliente> clientes;
    private ArrayList<GuiaTuristico> guias;
    private static final String RUTAUSERS = "src/main/resources/Data/users.txt";
    private static final String RUTADESTINOS = "src/main/resources/Data/destinos.txt";
    private static final String RUTAPAQUETES = "src/main/resources/Data/paquetes.ser";
    private static final String RUTAGUIAS = "src/main/resources/Data/guiasTuristicos.txt";

    private static final Logger LOGGER = Logger.getLogger(AgenciaUQ.class.getName());

    private AgenciaUQ() {
        inicializarLogger();
        log.info("Se cre una nueva instancia de AgenciaUQ");

        this.destinos = new ArrayList<>();
        leerDestinos();

        this.paquetes = new ArrayList<>();
        try {
            this.paquetes.addAll((ArrayList<PaqueteTuristico>) ArchivoUtils.deserializarObjeto(RUTAPAQUETES));
        } catch (IOException | ClassNotFoundException e) {
            log.severe(e.getMessage());
        }

        this.clientes = new ArrayList<>();
        leerClientes();
    }

    private void inicializarLogger() {
        try {
            FileHandler fh = new FileHandler("logs.log", true);
            fh.setFormatter(new SimpleFormatter());
            log.addHandler(fh);
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }

    //El singleton de la clase AgenciaUQApp
    public static AgenciaUQ getInstance() {
        if (agenciaUQ == null) {
            agenciaUQ = new AgenciaUQ();
        }
        return agenciaUQ;
    }

    public void agregarDestino(String pais, String ciudad, String clima, String descripcion, String imagen) throws AtributoVacioException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (pais == null || pais.isEmpty() || ciudad == null || ciudad.isEmpty() || clima == null || clima.isEmpty() || descripcion == null || descripcion.isEmpty() || imagen == null || imagen.isEmpty()) {
                    try {
                        throw new AtributoVacioException("Todos los atributos deben tener valores");
                    } catch (AtributoVacioException e) {
                        log.severe(e.getMessage());
                    }
                }
                Destino destino = Destino.builder().pais(pais).ciudad(ciudad).clima(clima).descripcion(descripcion).imagen(imagen).build();
                destinos.add(destino);
                escribirDestinos(destino);
                log.info("Destino '" + destino.getPais() + " - " + destino.getCiudad() + "' agregado correctamente");
                JOptionPane.showMessageDialog(null, "Destino agregado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        }).start();
    }

    public void eliminarDestino(String pais, String ciudad) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                eliminarDestinoRecursivo(destinos.iterator(), pais, ciudad);
            }
        }).start();
    }

    private void eliminarDestinoRecursivo(Iterator<Destino> iterator, String pais, String ciudad) {
        if (iterator.hasNext()) {
            Destino destino = iterator.next();
            if (destino.getPais().equals(pais) && destino.getCiudad().equals(ciudad)) {
                iterator.remove();
                borrarDestino(destino);
                eliminarDestinoRecursivo(iterator, pais, ciudad);
            } else {
                eliminarDestinoRecursivo(iterator, pais, ciudad);
            }
        }
    }

    private void borrarDestino(Destino destino) {
        String filePath = RUTADESTINOS;
        String lineToRemove = destino.getPais() + "¡" + destino.getCiudad() + "¡" + destino.getDescripcion() + "¡" + destino.getClima() + "¡" + destino.getImagen();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Leer el contenido del archivo y almacenarlo en una lista
                    Path path = Paths.get(filePath);
                    List<String> lines = Files.readAllLines(path);

                    // Buscar la línea que se desea borrar y removerla
                    lines = lines.stream().filter(line -> !line.contains(lineToRemove)).collect(Collectors.toList());

                    // Escribir el nuevo contenido de vuelta al archivo
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                        for (String updatedLine : lines) {
                            writer.write(updatedLine);
                            writer.newLine();
                        }
                    }

                    System.out.println("Línea eliminada exitosamente.");
                    log.info("Destino '" + destino.getPais() + " - " + destino.getCiudad() + "' eliminado correctamente");
                } catch (IOException e) {
                    log.severe(e.getMessage());
                }
            }
        }).start();
    }

    public void actualizarDestino(String pais, String ciudad) {
        for (Destino destino : destinos) {
            if (destino.getPais().equals(pais) && destino.getCiudad().equals(ciudad)) {
                Destino nuevo = new Destino();
                nuevo.setPais(pais);
                nuevo.setCiudad(ciudad);
                nuevo.setDescripcion(JOptionPane.showInputDialog("Ingrese la nueva descripcion"));
                nuevo.setClima(JOptionPane.showInputDialog("Ingrese el nuevo clima"));
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Seleccionar imagen");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg"));
                File archivoSeleccionado = fileChooser.showOpenDialog(null);
                String imagen = "";
                if (archivoSeleccionado != null) {
                    imagen = archivoSeleccionado.getAbsolutePath();
                }
                nuevo.setImagen(imagen);
                editarDestino(destino, nuevo);
                break;
            }
        }
    }

    public void editarDestino(Destino destino, Destino nuevo) {
        String filePath = RUTADESTINOS;
        String lineToReplace = destino.getPais() + "¡" + destino.getCiudad() + "¡" + destino.getDescripcion() + "¡" + destino.getClima() + "¡" + destino.getImagen();
        String newLine = nuevo.getPais() + "¡" + nuevo.getCiudad() + "¡" + nuevo.getDescripcion() + "¡" + nuevo.getClima() + "¡" + nuevo.getImagen();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Leer el contenido del archivo y almacenarlo en una lista
                    Path path = Paths.get(filePath);
                    List<String> lines = Files.readAllLines(path);

                    // Reemplazar la línea existente con la nueva línea
                    for (int i = 0; i < lines.size(); i++) {
                        if (lines.get(i).contains(lineToReplace)) {
                            lines.set(i, newLine);
                            break; // Solo necesitas reemplazar una vez
                        }
                    }

                    // Escribir el nuevo contenido de vuelta al archivo
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                        for (String updatedLine : lines) {
                            writer.write(updatedLine);
                            writer.newLine();
                        }
                    }

                    System.out.println("Línea reemplazada exitosamente.");
                    log.info("Destino '" + destino.getPais() + " - " + destino.getCiudad() + "' actualizado correctamente");
                } catch (IOException e) {
                    log.severe(e.getMessage());
                }
            }
        }).start();
    }

    private void leerDestinos() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<String> lineas = ArchivoUtils.leerArchivoScanner(RUTADESTINOS);

                    for (String linea : lineas) {
                        String[] datos = linea.split("¡");
                        destinos.add(Destino.builder().pais(datos[0]).ciudad(datos[1]).descripcion(datos[2]).clima(datos[3]).imagen(datos[4]).build());
                    }
                } catch (IOException e) {
                    log.severe(e.getMessage());
                }
            }
        }).start();
    }

    private void escribirDestinos(Destino destino) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String linea = destino.getPais() + "¡" + destino.getCiudad() + "¡" + destino.getDescripcion() + "¡" + destino.getClima() + "¡" + destino.getImagen();
                    ArchivoUtils.escribirArchivoBufferedWriter(RUTADESTINOS, List.of(linea), true);
                } catch (IOException e) {
                    log.severe(e.getMessage());
                }
            }
        }).start();
    }

    public static ArrayList<String> leerGuiasNombres() throws IOException {
        ArrayList<String> nombres = null;
        try {
            ArrayList<String> lineas = ArchivoUtils.leerArchivoBufferedReader(RUTAGUIAS);
            nombres = new ArrayList<>();
            for (String linea : lineas) {
                String[] val = linea.split(";");
                nombres.add(val[0]);
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
        return nombres;
    }

    public void añadirPaquete(String nombrePaquete, String descripcionPaquete, LocalDate fechaInicial, LocalDate fechaFinal, ArrayList<Destino> destinos) throws AtributoVacioException, IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (nombrePaquete == null || nombrePaquete.isEmpty() || descripcionPaquete == null || descripcionPaquete.isEmpty() || fechaInicial == null || fechaFinal == null || destinos == null || destinos.isEmpty()) {
                    try {
                        throw new AtributoVacioException("Uno o más atributos están vacíos o nulos");
                    } catch (AtributoVacioException e) {
                        log.severe(e.getMessage());
                    }
                }
                PaqueteTuristico paquete = PaqueteTuristico.builder().nombre(nombrePaquete).adicionales(descripcionPaquete).fechaInicio(fechaInicial).fechaFin(fechaFinal).destinoArrayList(destinos).build();
                paquetes.add(paquete);
                try {
                    ArchivoUtils.serializarObjeto(RUTAPAQUETES, paquetes);
                } catch (IOException e) {
                    log.severe(e.getMessage());
                }
                log.info("Paquete '" + nombrePaquete + "' agregado correctamente");
                JOptionPane.showMessageDialog(null, "Paquete agregado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        }).start();
    }

    public void actualizarPaquete(String nombre) throws IOException {
        for (PaqueteTuristico paquete : paquetes) {
            if (paquete.getNombre().equals(nombre)) {
                paquete.setAdicionales(JOptionPane.showInputDialog("Ingrese la nueva descripcion"));
                ArchivoUtils.serializarObjeto(RUTAPAQUETES, paquetes);
                break;
            }
        }
    }

    public void eliminarPaquete(String nombre) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (PaqueteTuristico paquete : paquetes) {
                    if (paquete.getNombre().equals(nombre)) {
                        paquetes.remove(paquete);
                        try {
                            ArchivoUtils.serializarObjeto(RUTAPAQUETES, paquetes);
                            JOptionPane.showMessageDialog(null, "Paquete eliminado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
                            log.info("Paquete '" + nombre + "' eliminado correctamente");
                        } catch (IOException e) {
                            log.severe(e.getMessage());
                        }
                        break;
                    }
                }
            }
        }).start();
    }

    public void iniciarSesion(String usuario, String clave) {
        try(BufferedReader br = new BufferedReader(new FileReader(RUTAUSERS))) {
            String line;
            boolean credencialesCorrectos = false;
            while((line = br.readLine()) != null) {
                String[] datos = line.split(",");
                if (datos.length >= 3 && datos[2].trim().equals(usuario) && datos[0].trim().equals(clave)) {
                    credencialesCorrectos = true;
                    IniciarSesionController.administrador = datos[6].equalsIgnoreCase("administrador");
                    break;
                }
            }
            if(credencialesCorrectos) {
                JOptionPane.showMessageDialog(null, "Bienvenido");
                IniciarSesionController.iniciado = true;
            } else {
                JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos");
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }

    public void registrarUsuario(String clave, String nombre, String documento, String email, String celular, String direccion, String rolSeleccionado) throws IOException {
        if (clave == null || clave.isEmpty() || nombre == null || nombre.isEmpty() || documento == null || documento.isEmpty() || email == null || email.isEmpty() || celular == null || celular.isEmpty() || direccion == null || direccion.isEmpty() || rolSeleccionado == null || rolSeleccionado.isEmpty()) {
            try {
                throw new AtributoVacioException("Todos los atributos deben tener valores");
            } catch (AtributoVacioException e) {
                log.severe(e.getMessage());
            }
        }
        if (encontrarDocumentoExistente(documento)) {
            JOptionPane.showMessageDialog(null, "Ya existe un usuario con ese documento");
        } else {
            Cliente cliente = Cliente.builder().clave(clave).nombreCompleto(nombre).cedula(documento).correo(email).telefono(celular).direccion(direccion).rol(rolSeleccionado).build();
            clientes.add(cliente);
            escribirUsuario(cliente);
            JOptionPane.showMessageDialog(null, "Usuario registrado correctamente");
            log.info("Usuario '" + cliente.getNombreCompleto() + "' registrado correctamente");
        }
    }

    private void escribirUsuario(Cliente cliente) {
        try {
            String linea = cliente.getClave() + "," + cliente.getNombreCompleto() + "," + cliente.getCedula() + "," + cliente.getCorreo() + "," + cliente.getTelefono() + "," + cliente.getDireccion() + "," + cliente.getRol();
            ArchivoUtils.escribirArchivoBufferedWriter(RUTAUSERS, List.of(linea), true);
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }

    private boolean encontrarDocumentoExistente(String documento) {
        try (BufferedReader br = new BufferedReader(new FileReader(RUTAUSERS))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] datos = line.split(",");
                if (datos.length > 2 && datos[2].trim().equals(documento.trim())) {
                    return true;
                }
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
        return false;
    }

    public void crearReserva (LocalDate fechaSolicitud, LocalDate fechaViaje, String idCliente, short numPersonas, PaqueteTuristico paqueteTuristico, Cliente cliente, GuiaTuristico guia, EstadoReserva estado) throws AtributoVacioException, FechaNoValidaException, CupoInvalidoException, IOException {

        if(fechaSolicitud.isAfter(fechaViaje)){
            LOGGER.log( Level.WARNING, "La fecha de solicitud no puede ser después de la fecha de viaje" );
            throw new FechaNoValidaException("La fecha de sloicitud no puede ser después de la fecha de viaje");
        }

        if(idCliente == null || idCliente.isBlank()){
            LOGGER.log( Level.WARNING, "La referencia es obligatoria para el registro" );
            throw new AtributoVacioException("La referencia es obligatoria");
        }
        if(!idCliente.matches("[0-9]+")){
            LOGGER.log( Level.WARNING, "La referencia no puede ser numérica" );
            throw new AtributoVacioException("La referencia no puede ser numérica");
        }

        Reserva reserva = Reserva.builder()
                .fechaSolicitud(fechaSolicitud)
                .fechaViaje(fechaViaje)
                .idCliente(idCliente)
                .numPersonas(numPersonas)
                .paqueteTuristico(paqueteTuristico)
                .cliente(cliente)
                .guia(guia)
                .estado(estado)
                .build();

        reservas.add(reserva);
        ArchivoUtils.escribirArchivoFormatter("src/main/resources/Data/reservas.txt", null);

        ArchivoUtils.mostrarMensaje("Informe", "", "Se ha agregado la reserva correctamente", Alert.AlertType.INFORMATION);
        LOGGER.log(Level.INFO, "Se ha registrado una nueva reserva del cliente: "+cliente);
    }

    public void modificarReserva(String idCliente, LocalDate nuevaFechaSolicitud, LocalDate nuevaFechaViaje, short nuevoNumPersonas, PaqueteTuristico nuevoPaqueteTuristico, Cliente nuevoCliente, GuiaTuristico nuevoGuia, EstadoReserva nuevoEstado) throws AtributoVacioException, FechaNoValidaException, CupoInvalidoException, IOException, ErrorGuardarCambios {

        // Buscar la reserva que se desea modificar
        Reserva reservaAModificar = null;
        for (Reserva reserva : reservas) {
            if (reserva.getCliente().getCedula().equals(idCliente)) {
                reservaAModificar = reserva;
                break;
            }
        }

        if (reservaAModificar == null) {
            ArchivoUtils.mostrarMensaje("Error", "Reserva no encontrada", "No se encontró ninguna reserva para el cliente con ID " + idCliente, Alert.AlertType.ERROR);
            throw new ErrorGuardarCambios("No se encontró ninguna reserva para el cliente con ID " + idCliente);
        }

        if (nuevaFechaSolicitud.isAfter(nuevaFechaViaje)) {
            LOGGER.log(Level.WARNING, "La fecha de solicitud no puede ser después de la fecha de viaje");
            throw new FechaNoValidaException("La fecha de solicitud no puede ser después de la fecha de viaje");
        }

        // Agregar más validaciones
        // Modificar la reserva
        reservaAModificar.setFechaSolicitud(nuevaFechaSolicitud);
        reservaAModificar.setFechaViaje(nuevaFechaViaje);
        reservaAModificar.setNumPersonas(nuevoNumPersonas);
        reservaAModificar.setPaqueteTuristico(nuevoPaqueteTuristico);
        reservaAModificar.setCliente(nuevoCliente);
        reservaAModificar.setGuia(nuevoGuia);
        reservaAModificar.setEstado(nuevoEstado);

        // Guardar la lista actualizada de reservas
        ArchivoUtils.escribirArchivoFormatter("src/main/resources/Data/reservas.txt", null);

        ArchivoUtils.mostrarMensaje("Informe", "", "Se ha modificado la reserva correctamente", Alert.AlertType.INFORMATION);
        LOGGER.log(Level.INFO, "Se ha modificado la reserva del cliente: " + idCliente);
    }

    public void eliminarReserva (String idCliente) throws ErrorGuardarCambios, IOException {
        // Buscar la reserva que se desea eliminar
        Reserva reservaAEliminar = null;
        for (Reserva reserva : reservas) {
            if (reserva.getCliente().getCedula().equals(idCliente)) {
                reservaAEliminar = reserva;
                break;
            }
        }

        if (reservaAEliminar != null) {
            reservas.remove(reservaAEliminar);
            ArchivoUtils.escribirArchivoFormatter("src/main/resources/Data/reservas.txt", null);

            ArchivoUtils.mostrarMensaje("Informe", "", "Se ha eliminado la reserva correctamente", Alert.AlertType.INFORMATION);
            LOGGER.log(Level.INFO, "Se ha eliminado la reserva del cliente con ID: " + idCliente);
        } else {
            ArchivoUtils.mostrarMensaje("Error", "Reserva no encontrada", "No se encontró ninguna reserva para el cliente con ID " + idCliente, Alert.AlertType.ERROR);
            throw new ErrorGuardarCambios("No se encontró ninguna reserva para el cliente con ID " + idCliente);
        }


    }


    public static ArrayList<String> leerNombresPaquetesTuristicos() throws IOException {
        ArrayList<String> nombres = new ArrayList<>();
        try {
            ArrayList<String> lineas = (ArrayList<String>) ArchivoUtils.deserializarObjeto(RUTAPAQUETES);
            for (String linea : lineas) {
                String[] val = linea.split(";");
                nombres.add(val[0]);
            }
        } catch (IOException e) {e.getMessage();} catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return nombres;
    }

    public Cliente clienteEnLista(String nombreCliente) {
        for (Cliente cliente : clientes) {
            if (cliente.getNombreCompleto().equals(nombreCliente)) return cliente;
        }
        return null;
    }

    public GuiaTuristico obtenerGuiaPorNombre(String nombreGuia) {
        for (GuiaTuristico guia : guias) {
            if (guia.getNombre().equals(nombreGuia)) {
                return guia;
            }
        }
        return null;
    }

    public PaqueteTuristico obtenerPaquetePorNombre(String nombrePaquete) {
        for (PaqueteTuristico paquete : paquetes) {
            if (paquete.getNombre().equals(nombrePaquete)) {
                return paquete;
            }
        }
        return null;
    }

    public boolean obtenerClienteCedula(String documento) {
        for (Cliente cliente : clientes) {
            if (cliente.getCedula().equals(documento)) return true;
        }
        return false;
    }

    private void leerClientes() {
        try (BufferedReader br = new BufferedReader(new FileReader(RUTAUSERS))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] datos = line.split(",");
                if (datos.length > 2 && datos[6].trim().equalsIgnoreCase("cliente")) {
                    clientes.add(Cliente.builder().clave(datos[0]).nombreCompleto(datos[1]).cedula(datos[2]).correo(datos[3]).telefono(datos[4]).direccion(datos[5]).rol(datos[6]).build());
                }
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
    }



    public void modificarUsuario(String cedula, String nuevoNombre, String nuevoCorreo, String nuevoTelefono, String nuevaDireccion, String nuevaContraseña) throws IOException, ErrorGuardarCambios, ClassNotFoundException {

        // Obtener la lista actualizada de clientes
        ArrayList<Cliente> listaClientes = (ArrayList<Cliente>) ArchivoUtils.deserializarObjeto("src/main/resources/Data/users.data");
        Optional<Cliente> clienteOptional = listaClientes.stream()
                .filter(cliente -> cliente.getCedula().equals(cedula))
                .findFirst();

        if (clienteOptional.isPresent()) {
            Cliente cliente = Cliente.builder()
                    .cedula(cedula)
                    .nombreCompleto(nuevoNombre)
                    .correo(nuevoCorreo)
                    .telefono(nuevoTelefono)
                    .direccion(nuevaDireccion)
                    .clave(nuevaContraseña)
                    .build();

            // Guardar la lista actualizada de clientes
            ArchivoUtils.escribirArchivoFormatter("src/main/resources/Data/users.data", null);
            ArchivoUtils.mostrarMensaje("Informe", "", "Se ha modificado el usuario correctamente", Alert.AlertType.INFORMATION);
            LOGGER.log(Level.INFO, "Se ha modificado el usuario con cédula: " + cedula);

        } else {
            ArchivoUtils.mostrarMensaje("Error", "Cliente no encontrado", "No se encontró ningún cliente con la cédula proporcionada.", Alert.AlertType.ERROR);
            throw new ErrorGuardarCambios("No se encontró ningún cliente con la cédula proporcionada.");
        }
    }
}