package org.jquran.jquran;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Verse {
    private int id, verse_number, hizb_number, rub_el_hizb_number, ruku_number, manzil_number, sajdah_number, chapter_id, page_number, juz_number;

    private String text_uthmani, text_imlaei_simple;
    //    private String verse_key, text_uthmani_simple, text_imlaei, code_v1, code_v2;
    private List<Word> words;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVerse_number() {
        return verse_number;
    }

    public void setVerse_number(int verse_number) {
        this.verse_number = verse_number;
    }

    public int getHizb_number() {
        return hizb_number;
    }

    public void setHizb_number(int hizb_number) {
        this.hizb_number = hizb_number;
    }

    public int getRub_el_hizb_number() {
        return rub_el_hizb_number;
    }

    public void setRub_el_hizb_number(int rub_el_hizb_number) {
        this.rub_el_hizb_number = rub_el_hizb_number;
    }

    public int getRuku_number() {
        return ruku_number;
    }

    public void setRuku_number(int ruku_number) {
        this.ruku_number = ruku_number;
    }

    public int getManzil_number() {
        return manzil_number;
    }

    public void setManzil_number(int manzil_number) {
        this.manzil_number = manzil_number;
    }

    public int getSajdah_number() {
        return sajdah_number;
    }

    public void setSajdah_number(int sajdah_number) {
        this.sajdah_number = sajdah_number;
    }

    public int getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

    public int getPage_number() {
        return page_number;
    }

    public void setPage_number(int page_number) {
        this.page_number = page_number;
    }

    public int getJuz_number() {
        return juz_number;
    }

    public void setJuz_number(int juz_number) {
        this.juz_number = juz_number;
    }

    public String getText_uthmani() {
        return text_uthmani;
    }

    public void setText_uthmani(String text_uthmani) {
        this.text_uthmani = text_uthmani;
    }

    public String getText_imlaei_simple() {
        return text_imlaei_simple;
    }

    public void setText_imlaei_simple(String text_imlaei_simple) {
        this.text_imlaei_simple = text_imlaei_simple;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }
}
