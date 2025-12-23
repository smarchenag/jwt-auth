package co.example.r2dbc.repository;

import co.example.r2dbc.entity.RolEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RolAdapterRepository extends ReactiveCrudRepository<RolEntity,Long> {

    @Query("SELECT r.id_rol, r.name_rol, r.descripcion " +
            "FROM rol r " +
            "INNER JOIN usuarios_roles ur ON r.id_rol = ur.id_rol " +
            "WHERE ur.id_usuario = :idUsuario")
    Flux<RolEntity> findUserRoles(Long idUsuario);

}
