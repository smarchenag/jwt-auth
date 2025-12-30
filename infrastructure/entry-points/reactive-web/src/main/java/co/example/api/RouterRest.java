package co.example.api;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.example.api.validator.LoginValidator;
import co.example.api.validator.RegistroValidator;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

    @Bean
    public LoginValidator loginValidator() {
        return new LoginValidator();
    }

    @Bean
    public RegistroValidator registroValidator() {
        return new RegistroValidator();
    }

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/login"), handler::loginUsuario)
                .andRoute(POST("/registro"), handler::registroUsuario)
                ;
    }
}

