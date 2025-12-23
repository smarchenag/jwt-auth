package co.example.r2dbc.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("usuarios_roles")
public class UsuarioRolEntity {
    private Long idUsuario;
    private Long idRol;
}
