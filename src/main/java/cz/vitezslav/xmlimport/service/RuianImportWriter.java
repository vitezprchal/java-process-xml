package cz.vitezslav.xmlimport.service;

import cz.vitezslav.xmlimport.repository.CastObceRepository;
import cz.vitezslav.xmlimport.repository.ObecRepository;
import cz.vitezslav.xmlimport.xml.ParsedRuianData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RuianImportWriter {

    private final ObecRepository obecRepository;
    private final CastObceRepository castObceRepository;

    @Transactional
    public void persist(ParsedRuianData parsedRuianData) {
        obecRepository.upsert(parsedRuianData.obec());
        castObceRepository.replaceAllForObec(parsedRuianData.obec().kod(), parsedRuianData.castiObci());
    }
}
