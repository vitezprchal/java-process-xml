package cz.vitezslav.xmlimport.xml;

import cz.vitezslav.xmlimport.domain.CastObceRecord;
import cz.vitezslav.xmlimport.domain.ObecRecord;

import java.util.List;

public record ParsedRuianData(ObecRecord obec, List<CastObceRecord> castiObci) {
}
