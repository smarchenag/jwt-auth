package co.example.r2dbc.repository;

import co.example.r2dbc.entity.UsuarioRolEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface UsuarioRolAdapterRepository extends ReactiveCrudRepository<UsuarioRolEntity,Long> {

    @Query("SELECT * FROM usuarios_roles WHERE id_usuario = :idUsuario")
    Flux<UsuarioRolEntity> findRolesByUserId(Long idUsuario);
}
