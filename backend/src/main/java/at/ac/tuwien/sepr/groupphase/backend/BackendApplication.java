package at.ac.tuwien.sepr.groupphase.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This class represents the entry point for running the application.
 * The annotation {@code @SpringBootApplication} indicates it's a Spring Boot application.
 * It contains the {@code main} method, which initializes and runs the application
 */
@SpringBootApplication
public class BackendApplication {

    /**
     * The main method of the application.
     * This method initializes and runs the Spring application context, effectively starting the application.
     *
     * @param args the command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
