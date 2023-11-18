package Modelo;

import Controladores.IniciarSesionController;
import Controladores.PrincipalController;
import Exceptions.AtributoVacioException;
import Utils.ArchivoUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.extern.java.Log;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;
import Enum.Idioma;
@Log
public class AgenciaUQ {
    @Getter
    private static AgenciaUQ agenciaUQ;
    @Getter
    private final ArrayList<Destino> destinos;
    @Getter
    private final ArrayList<PaqueteTuristico> paquetes;
    @Getter
    private final ArrayList<Cliente> clientes;
    private String imagen;
    private ArrayList<GuiaTuristico> guias;
    private static final String RUTAUSERS = "src/main/resources/Data/users.txt";
    private static final String RUTADESTINOS = "src/main/resources/Data/destinos.txt";
    private static final String RUTAPAQUETES = "src/main/resources/Data/paquetes.ser";
    private static final String RUTAGUIAS = "src/main/resources/Data/guiasTuristicos.txt";

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
                seleccionarImagen();
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

    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg"));
        File archivoSeleccionado = fileChooser.showOpenDialog(null);
        if (archivoSeleccionado != null) {
            imagen = archivoSeleccionado.getAbsolutePath();
        }
    }

    /**
     * Se inicializan los guias turisticos que estan en el archivo guiasTuristicos.Txt
     *
     * @return
     */
    public void leerGuias() throws IOException {
        try {
            ArrayList<String> lineas = ArchivoUtils.leerArchivoBufferedReader(RUTAGUIAS);
            for (String linea : lineas) {
                String[] val = linea.split(";");
                this.guias.add(GuiaTuristico.builder()
                        .nombre(val[0])
                        .identificacion(val[1])
                        .idioma(Idioma.valueOf(val[2]))
                        .telefono(val[3])
                        .calificacion(Double.parseDouble(val[4])).build());
            }
        } catch (IOException e) {
            log.severe(e.getMessage());
        }
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
                try {
                    Node node = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Interfaces/SesionIniciada.fxml")));
                    PrincipalController.getInstance().panelFormulario.getChildren().setAll(node);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
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
}