package cz.vitezslav.xmlimport.xml;

enum RuianXmlLocalName {

    OBCE("Obce"),
    OBEC("Obec"),
    CAST_OBCE("CastObce"),
    KOD("Kod"),
    NAZEV("Nazev");

    private final String localName;

    RuianXmlLocalName(String localName) {
        this.localName = localName;
    }

    boolean matches(String readerLocalName) {
        return localName.equals(readerLocalName);
    }
}
