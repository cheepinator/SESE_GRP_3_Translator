package com.sese.translator.web.rest.parsing;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class ElementHandler extends DefaultHandler {
    private boolean inString = false;
    private String name = null;
    private StringBuffer content = null;
    private List<DefinitionExtraction> definitions;

    public ElementHandler() {
    }

    @Override
    public void startDocument() throws SAXException {
        definitions = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("string")) {
            inString = true;
            name = attributes.getValue("name");
            content = new StringBuffer();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("string")) {
            definitions.add(new DefinitionExtraction(name, content.toString()));
            System.out.println("new Definition: " + name + " " + content);
            inString = false;
            name = null;
            content = null;
        }
    }

    @Override
    public void characters(char[] chars, int i, int i1) throws SAXException {
        if (inString) {
            content.append(new String(chars, i, i1));
        }
    }

    public List<DefinitionExtraction> getDefinitions() {
        return definitions;
    }
}
