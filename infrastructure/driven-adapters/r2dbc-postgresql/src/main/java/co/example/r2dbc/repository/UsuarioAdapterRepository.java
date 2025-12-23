package co.example.r2dbc.repository;

import co.example.r2dbc.entity.UsuarioEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UsuarioAdapterRepository extends ReactiveCrudRepository<UsuarioEntity,Long> {

    @Query("SELECT * FROM usuarios WHERE email = :email")
    Mono<UsuarioEntity> findUserByEmail(String email);

}
