package data.lineage.processing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
