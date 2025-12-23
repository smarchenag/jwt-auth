package co.example.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("usuarios")
public class UsuarioEntity {
    @Id
    @Column("id_usuario")
    private Long idUsuario;
    @Column("name_usuario")
    private String name;
    private String email;
    @Column("password_usuario")
    private String password;

}
