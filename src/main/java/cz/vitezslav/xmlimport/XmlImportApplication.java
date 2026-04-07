package cz.vitezslav.xmlimport;

import cz.vitezslav.xmlimport.config.ImportProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ImportProperties.class)
public class XmlImportApplication {

    public static void main(String[] args) {
        SpringApplication.run(XmlImportApplication.class, args);
    }
}
