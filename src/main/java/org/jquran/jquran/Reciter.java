package org.jquran.jquran;

public class Reciter {

    private int id;
    private String reciter_name, style;
    private TranslatedName translated_name;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReciter_name() {
        return reciter_name;
    }

    public void setReciter_name(String reciter_name) {
        this.reciter_name = reciter_name;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public TranslatedName getTranslated_name() {
        return translated_name;
    }

    public void setTranslated_name(TranslatedName translated_name) {
        this.translated_name = translated_name;
    }
}