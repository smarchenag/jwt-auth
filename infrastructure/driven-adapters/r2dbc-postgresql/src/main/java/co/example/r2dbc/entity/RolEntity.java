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
@Table("rol")
public class RolEntity {
    @Id
    @Column("id_rol")
    private Long id;
    @Column("name_rol")
    private String name;
    @Column("descripcion")
    private String descripcion;
}
