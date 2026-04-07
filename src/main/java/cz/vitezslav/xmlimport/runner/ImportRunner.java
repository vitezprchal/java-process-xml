package cz.vitezslav.xmlimport.runner;

import cz.vitezslav.xmlimport.service.RuianImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImportRunner implements CommandLineRunner {

    private final RuianImportService ruianImportService;

    @Override
    public void run(String... args) {
        ruianImportService.importData();
    }
}
