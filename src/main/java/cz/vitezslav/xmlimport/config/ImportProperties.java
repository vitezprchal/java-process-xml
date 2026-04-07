package cz.vitezslav.xmlimport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.import")
public record ImportProperties(String xmlPath) {
}
