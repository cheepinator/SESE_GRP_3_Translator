package com.sese.translator.web.rest.parsing;

/**
 * Created by jobro on 15.01.2017.
 */
public class DefinitionExtraction {
    private String code;
    private String text;

    public DefinitionExtraction(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
