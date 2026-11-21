package sn.uasz.guidetouristique;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GuideTouristiqueApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuideTouristiqueApplication.class, args);
    }
}
