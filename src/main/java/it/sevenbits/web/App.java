package it.sevenbits.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication // специальная аннотация, помечающая данный класс как веб-сервер, конфигурационный, с автоматическим поиском других конфигов.
public class App extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.run(args);
    }
}
