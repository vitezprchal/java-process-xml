package cz.vitezslav.xmlimport.xml;

import cz.vitezslav.xmlimport.domain.CastObceRecord;
import cz.vitezslav.xmlimport.domain.ObecRecord;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class RuianXmlParser {

    private final XMLInputFactory xmlInputFactory;

    public RuianXmlParser() {
        this.xmlInputFactory = XMLInputFactory.newFactory();
        this.xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        this.xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        this.xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    }

    public ParsedRuianData parse(Path xmlPath) {
        try (InputStream inputStream = Files.newInputStream(xmlPath)) {
            return parse(inputStream);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot read XML file: " + xmlPath, ex);
        }
    }

    public ParsedRuianData parse(InputStream inputStream) {
        try {
            return parseDocument(inputStream);
        } catch (XMLStreamException ex) {
            throw new IllegalStateException("Cannot parse XML stream", ex);
        }
    }

    private ParsedRuianData parseDocument(InputStream inputStream) throws XMLStreamException {
        XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(inputStream);
        try {
            RuianParseContext ctx = new RuianParseContext();
            while (reader.hasNext()) {
                int eventType = reader.next();
                if (eventType == XMLStreamConstants.START_ELEMENT) {
                    onStartElement(reader, ctx);
                } else if (eventType == XMLStreamConstants.END_ELEMENT) {
                    onEndElement(reader, ctx);
                }
            }
            return buildResult(ctx);
        } finally {
            reader.close();
        }
    }

    private void onStartElement(XMLStreamReader reader, RuianParseContext ctx) throws XMLStreamException {
        String local = reader.getLocalName();
        String parent = ctx.ancestorName(0);
        String grandParent = ctx.ancestorName(1);

        ctx.pushElement(local);

        if (RuianXmlLocalName.OBEC.matches(local) && RuianXmlLocalName.OBCE.matches(parent)) {
            parseMainObecStart(ctx);
            return;
        }
        if (RuianXmlLocalName.CAST_OBCE.matches(local)) {
            parseCastObceStart(ctx);
            return;
        }
        if (readObecChildFieldIfPresent(reader, ctx, local, parent)) {
            return;
        }
        if (readCastObceChildFieldIfPresent(reader, ctx, local, parent, grandParent)) {
            return;
        }
    }

    private void parseMainObecStart(RuianParseContext ctx) {
        ctx.enterMainObec();
    }

    private void parseCastObceStart(RuianParseContext ctx) {
        ctx.beginCastObce();
    }

    private boolean readObecChildFieldIfPresent(XMLStreamReader reader, RuianParseContext ctx, String local, String parent)
            throws XMLStreamException {
        if (!ctx.isInMainObec() || !RuianXmlLocalName.OBEC.matches(parent)) {
            return false;
        }
        if (RuianXmlLocalName.KOD.matches(local)) {
            ctx.setObecKod(readElementTextTrimmed(reader));
            ctx.popElement();
            return true;
        }
        if (RuianXmlLocalName.NAZEV.matches(local)) {
            ctx.setObecNazev(readElementTextTrimmed(reader));
            ctx.popElement();
            return true;
        }
        return false;
    }

    private boolean readCastObceChildFieldIfPresent(
            XMLStreamReader reader,
            RuianParseContext ctx,
            String local,
            String parent,
            String grandParent) throws XMLStreamException {
        if (!ctx.isInCastObce()) {
            return false;
        }
        if (RuianXmlLocalName.CAST_OBCE.matches(parent)) {
            if (RuianXmlLocalName.KOD.matches(local)) {
                ctx.setCastKod(readElementTextTrimmed(reader));
                ctx.popElement();
                return true;
            }
            if (RuianXmlLocalName.NAZEV.matches(local)) {
                ctx.setCastNazev(readElementTextTrimmed(reader));
                ctx.popElement();
                return true;
            }
        }
        if (RuianXmlLocalName.OBEC.matches(parent)
                && RuianXmlLocalName.CAST_OBCE.matches(grandParent)
                && RuianXmlLocalName.KOD.matches(local)) {
            ctx.setCastKodObce(readElementTextTrimmed(reader));
            ctx.popElement();
            return true;
        }
        return false;
    }

    private void onEndElement(XMLStreamReader reader, RuianParseContext ctx) {
        String local = reader.getLocalName();
        ctx.popElement();

        if (RuianXmlLocalName.OBEC.matches(local) && ctx.isInMainObec()) {
            parseMainObecEnd(ctx);
        } else if (RuianXmlLocalName.CAST_OBCE.matches(local) && ctx.isInCastObce()) {
            parseCastObceEnd(ctx);
        }
    }

    private void parseMainObecEnd(RuianParseContext ctx) {
        ctx.leaveMainObec();
    }

    private void parseCastObceEnd(RuianParseContext ctx) {
        ctx.leaveCastObce();
    }

    private ParsedRuianData buildResult(RuianParseContext ctx) {
        String obecKod = ctx.getObecKod();
        String obecNazev = ctx.getObecNazev();
        if (obecKod == null || obecNazev == null) {
            throw new IllegalStateException("Missing required vf:Obec fields (Kod, Nazev) in XML.");
        }
        List<CastObceRecord> casti = List.copyOf(ctx.getCastiObci());
        return new ParsedRuianData(new ObecRecord(obecKod, obecNazev), casti);
    }

    private static String readElementTextTrimmed(XMLStreamReader reader) throws XMLStreamException {
        return trimToNull(reader.getElementText());
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
