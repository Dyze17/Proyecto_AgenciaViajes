package Modelo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {
    private String cedula;
    private String nombreCompleto;
    private String correo;
    private String telefono;
    private String direccion;
    private String contraseña;
}