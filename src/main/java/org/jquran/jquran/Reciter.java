package org.jquran.jquran;

public class Reciter {

    private int id, reciter_id;
    private String name_en, name_ar, style, ar_style;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReciter_id() {
        return reciter_id;
    }

    public void setReciter_id(int reciter_id) {
        this.reciter_id = reciter_id;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName_ar() {
        return name_ar;
    }

    public void setName_ar(String name_ar) {
        this.name_ar = name_ar;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getAr_style() {
        return ar_style;
    }

    public void setAr_style(String ar_style) {
        this.ar_style = ar_style;
    }

    @Override
    public String toString() {
        if(style.equals("Murattal")) return getName_ar();
        return getName_ar() + " - " + getAr_style();
    }
}