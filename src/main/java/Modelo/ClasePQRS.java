package Modelo;

import Enum.EstadoPQRS;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClasePQRS {
    private String nombre;
    private EstadoPQRS pqrs;
    private String mensaje;
}
