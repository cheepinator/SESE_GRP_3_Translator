package com.sese.translator.web.rest.parsing;

import com.sese.translator.web.rest.TranslationResource;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by jobro on 15.01.2017.
 */
public class ElementHandler extends DefaultHandler {
    private TranslationResource resource;
    private boolean inString=false;
    private String name = null;
    private StringBuffer content = null;

    public ElementHandler(TranslationResource resource) {
        this.resource = resource;
    }

    @Override
    public void setDocumentLocator(Locator locator) {

    }

    @Override
    public void startDocument() throws SAXException {
        this.resource.definitions = new ArrayList<DefinitionExtraction>();
    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startPrefixMapping(String s, String s1) throws SAXException {

    }

    @Override
    public void endPrefixMapping(String s) throws SAXException {

    }



    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(qName.equals("string")){
            inString = true;
            name = attributes.getValue("name");
            content = new StringBuffer();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if(qName.equals("string")){
            this.resource.definitions.add(new DefinitionExtraction(name, content.toString()));
            System.out.println("new Definition: "+name + " "+content);
            inString = false;
            name = null;
            content = null;
        }
    }

    @Override
    public void characters(char[] chars, int i, int i1) throws SAXException {
        if(inString){
            content.append(new String(chars, i, i1));
        }
    }

    @Override
    public void ignorableWhitespace(char[] chars, int i, int i1) throws SAXException {

    }

    @Override
    public void processingInstruction(String s, String s1) throws SAXException {

    }

    @Override
    public void skippedEntity(String s) throws SAXException {

    }
}
