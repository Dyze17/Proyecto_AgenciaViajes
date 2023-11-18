package Controladores;

import Exceptions.AtributoVacioException;
import Exceptions.CupoInvalidoException;
import Exceptions.ErrorGuardarCambios;
import Exceptions.FechaNoValidaException;
import Modelo.*;
import Utils.ArchivoUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import Enum.EstadoReserva;
import javafx.scene.layout.AnchorPane;

public class ReservaAgregarController implements Initializable {

    @FXML
    private ChoiceBox<Short> boxNumPersonas;
    @FXML
    private Button btnNueva;
    @FXML
    private TextField txtNombre;
    @FXML
    private ChoiceBox<EstadoReserva> boxEstado;
    @FXML
    private ChoiceBox<String> boxGuia;
    @FXML
    private ChoiceBox<String> boxPaquete;
    @FXML
    private AnchorPane paneReservaAgregar;
    @FXML
    private DatePicker dateSolicitud;
    @FXML
    private TextField txtDocumento;
    @FXML
    private DatePicker dateViaje;

    private Cliente cliente;
    private Reserva reserva;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boxEstado.getItems().addAll(EstadoReserva.values());
                for (short i = 1; i <= 20; i++) {
                    boxNumPersonas.getItems().add(i);
                }
                try {
                    ArrayList<String> nombresGuias = AgenciaUQ.leerGuiasNombres();
                    for(String nombre : nombresGuias) {
                        boxGuia.getItems().add(nombre);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    ArrayList<String> nombresPaquete = AgenciaUQ.leerNombresPaquetesTuristicos();
                    for(String nombre : nombresPaquete) {
                        boxPaquete.getItems().add(nombre);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @FXML
    public void showNuevaReserva(ActionEvent event) throws ErrorGuardarCambios {
        String nombreCliente = txtNombre.getText();
        LocalDate fechaSolicitud = dateSolicitud.getValue();
        String documento = txtDocumento.getText();
        LocalDate fechaViaje = dateViaje.getValue();
        String nombreGuia = boxGuia.getValue();
        Short numPer = boxNumPersonas.getValue();
        String paquete = boxPaquete.getValue();
        EstadoReserva estado = boxEstado.getValue();

        Cliente user = AgenciaUQ.getInstance().clienteEnLista(nombreCliente);
        if (user == null) {
            ArchivoUtils.mostrarMensaje("Error", "Cliente no encontrado", "El cliente ingresado no está en la lista.", Alert.AlertType.ERROR);
            throw new ErrorGuardarCambios("el cliente no esta en la lista");
        }
        GuiaTuristico guia = AgenciaUQ.getInstance().obtenerGuiaPorNombre(nombreGuia);
        if (guia == null) {
            ArchivoUtils.mostrarMensaje("Error", "Guia no encontrado", "El guia ingresado no está en la lista.", Alert.AlertType.ERROR);
            throw new ErrorGuardarCambios("el cliente no esta en la lista");
        }
        PaqueteTuristico nuevoPaquete = AgenciaUQ.getInstance().obtenerPaquetePorNombre(paquete);
        if (nuevoPaquete == null) {
            ArchivoUtils.mostrarMensaje("Error", "Paquete no encontrado", "El Paquete ingresado no está en la lista.", Alert.AlertType.ERROR);
            throw new ErrorGuardarCambios("el cliente no esta en la lista");
        }

        // Crear la reserva
        try {
            AgenciaUQ.getInstance().crearReserva(fechaSolicitud, fechaViaje, documento, numPer, nuevoPaquete, user, guia, estado);
            ArchivoUtils.mostrarMensaje("Éxito", "Reserva exitosa", "La reserva se ha creado correctamente.", Alert.AlertType.INFORMATION);
        } catch (AtributoVacioException | FechaNoValidaException | CupoInvalidoException | IOException e) {
            ArchivoUtils.mostrarMensaje("Error", "Error al crear reserva", "Hubo un error al intentar crear la reserva.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }


}