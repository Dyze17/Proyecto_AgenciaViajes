package Controladores;

import Exceptions.AtributoVacioException;
import Exceptions.CupoInvalidoException;
import Exceptions.ErrorGuardarCambios;
import Exceptions.FechaNoValidaException;
import Modelo.AgenciaUQ;
import Modelo.Cliente;
import Modelo.GuiaTuristico;
import Modelo.PaqueteTuristico;
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

public class ReservaModificarController implements Initializable {
    @FXML
    private ChoiceBox<Short> boxNumPersonas;
    @FXML
    private TextField txtNombre;
    @FXML
    private Button btnEliminar;
    @FXML
    private ChoiceBox<String> boxGuia;
    @FXML
    private ChoiceBox<EstadoReserva> boxEstado;
    @FXML
    private ChoiceBox<String> boxPaquete;
    @FXML
    private DatePicker dateSolicitud;
    @FXML
    private Button btnModificar;
    @FXML
    private TextField txtDocumento;
    @FXML
    private DatePicker dateViaje;

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
    void showModificar(ActionEvent event) throws ErrorGuardarCambios {
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

        try {
            AgenciaUQ.getInstance().modificarReserva(documento, fechaSolicitud, fechaViaje, numPer, nuevoPaquete, user, guia, estado);
            ArchivoUtils.mostrarMensaje("Éxito", "Reserva exitosa", "La reserva se ha creado correctamente.", Alert.AlertType.INFORMATION);
        } catch (AtributoVacioException | FechaNoValidaException | CupoInvalidoException | IOException e) {
            ArchivoUtils.mostrarMensaje("Error", "Error al crear reserva", "Hubo un error al intentar crear la reserva.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void showEliminar(ActionEvent event) {
        ArchivoUtils.mostrarMensaje("Informe", "Informacion para eliminar", "Para poder eliminar la reserva solo con el documento puedes eliminarla", Alert.AlertType.INFORMATION);
        String documento = txtDocumento.getText();
        try {
            AgenciaUQ.getInstance().eliminarReserva(documento);
            ArchivoUtils.escribirArchivoFormatter("src/main/resources/Data/reservas.data", null);
            ArchivoUtils.mostrarMensaje("Éxito", "Reserva eliminada", "La reserva se ha eliminado correctamente.", Alert.AlertType.INFORMATION);
        } catch (ErrorGuardarCambios | IOException e) {
            ArchivoUtils.mostrarMensaje("Error", "Error al eliminar reserva", "Hubo un error al intentar eliminar la reserva.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

}
