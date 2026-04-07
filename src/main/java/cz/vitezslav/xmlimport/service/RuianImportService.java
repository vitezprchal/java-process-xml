package cz.vitezslav.xmlimport.service;

import cz.vitezslav.xmlimport.config.ImportProperties;
import cz.vitezslav.xmlimport.xml.ParsedRuianData;
import cz.vitezslav.xmlimport.xml.RuianXmlParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuianImportService {

    private final ImportProperties importProperties;
    private final RuianXmlParser ruianXmlParser;
    private final RuianImportWriter ruianImportWriter;

    public void importData() {
        Path xmlPath = resolveXmlPath(importProperties.xmlPath());

        if (!Files.exists(xmlPath)) {
            throw new IllegalStateException("Configured XML file does not exist: " + xmlPath);
        }

        ParsedRuianData parsedRuianData = ruianXmlParser.parse(xmlPath);

        ruianImportWriter.persist(parsedRuianData);

        log.info("Import finished for obec {} ({})", parsedRuianData.obec().nazev(), parsedRuianData.obec().kod());
        log.info("Imported {} casti obce", parsedRuianData.castiObci().size());
    }

    private Path resolveXmlPath(String configuredPath) {
        if (!StringUtils.hasText(configuredPath)) {
            throw new IllegalStateException("Property app.import.xml-path must be set.");
        }
        return Path.of(configuredPath).toAbsolutePath().normalize();
    }
}
