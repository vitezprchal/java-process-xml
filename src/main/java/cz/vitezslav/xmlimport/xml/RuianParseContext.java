package cz.vitezslav.xmlimport.xml;

import cz.vitezslav.xmlimport.domain.CastObceRecord;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

final class RuianParseContext {

    private final Deque<String> elementStack = new ArrayDeque<>();
    @Getter
    private final List<CastObceRecord> castiObci = new ArrayList<>();

    @Getter
    @Setter
    private String obecKod;
    @Getter
    @Setter
    private String obecNazev;

    private boolean inMainObec;
    private boolean inCastObce;

    @Setter
    private String castKod;
    @Setter
    private String castNazev;
    @Setter
    private String castKodObce;

    void pushElement(String localName) {
        elementStack.push(localName);
    }

    void popElement() {
        if (!elementStack.isEmpty()) {
            elementStack.pop();
        }
    }

    String ancestorName(int depthFromTop) {
        Iterator<String> it = elementStack.iterator();
        String name = null;
        for (int i = 0; i <= depthFromTop; i++) {
            if (!it.hasNext()) {
                return null;
            }
            name = it.next();
        }
        return name;
    }

    void enterMainObec() {
        inMainObec = true;
    }

    void leaveMainObec() {
        inMainObec = false;
    }

    boolean isInMainObec() {
        return inMainObec;
    }

    void beginCastObce() {
        inCastObce = true;
        castKod = null;
        castNazev = null;
        castKodObce = null;
    }

    void leaveCastObce() {
        inCastObce = false;
        if (castKod != null && castNazev != null && castKodObce != null) {
            castiObci.add(new CastObceRecord(castKod, castNazev, castKodObce));
        }
    }

    boolean isInCastObce() {
        return inCastObce;
    }
}
