package org.jquran.jquran;

import java.util.List;

public class Chapter {
    private int id, verses_count, revelation_order;
    private String revelation_place, name_simple, name_arabic, name_complex;
    private boolean bismillah_pre;
    private List<Integer> pages;
    private TranslatedName translated_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVerses_count() {
        return verses_count;
    }

    public void setVerses_count(int verses_count) {
        this.verses_count = verses_count;
    }

    public int getRevelation_order() {
        return revelation_order;
    }

    public void setRevelation_order(int revelation_order) {
        this.revelation_order = revelation_order;
    }

    public String getRevelation_place() {
        return revelation_place;
    }

    public void setRevelation_place(String revelation_place) {
        this.revelation_place = revelation_place;
    }

    public String getName_simple() {
        return name_simple;
    }

    public void setName_simple(String name_simple) {
        this.name_simple = name_simple;
    }

    public String getName_arabic() {
        return name_arabic;
    }

    public void setName_arabic(String name_arabic) {
        this.name_arabic = name_arabic;
    }

    public String getName_complex() {
        return name_complex;
    }

    public void setName_complex(String name_complex) {
        this.name_complex = name_complex;
    }

    public boolean isBismillah_pre() {
        return bismillah_pre;
    }

    public void setBismillah_pre(boolean bismillah_pre) {
        this.bismillah_pre = bismillah_pre;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public TranslatedName getTranslated_name() {
        return translated_name;
    }

    public void setTranslated_name(TranslatedName translated_name) {
        this.translated_name = translated_name;
    }
    @Override
    public String toString() {
        return getId() + ". " + getName_arabic();
//        return "رقمها" + "_" + surahNumber + "_" + "آياتها"+ "_" + verseCount + "_" + place;
    }
}