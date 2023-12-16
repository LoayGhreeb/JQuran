package org.jquran.jquran;
import java.util.List;

public class Verse {
    private int id, verse_number, hizb_number, rub_el_hizb_number, ruku_number, manzil_number, sajdah_number, chapter_id, page_number, juz_number;
    private String verse_key, text_uthmani_simple, text_imlaei, code_v1, code_v2;
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

    public String getVerse_key() {
        return verse_key;
    }

    public void setVerse_key(String verse_key) {
        this.verse_key = verse_key;
    }

    public String getText_uthmani_simple() {
        return text_uthmani_simple;
    }

    public void setText_uthmani_simple(String text_uthmani_simple) {
        this.text_uthmani_simple = text_uthmani_simple;
    }

    public String getText_imlaei() {
        return text_imlaei;
    }

    public void setText_imlaei(String text_imlaei) {
        this.text_imlaei = text_imlaei;
    }

    public String getCode_v1() {
        return code_v1;
    }

    public void setCode_v1(String code_v1) {
        this.code_v1 = code_v1;
    }

    public String getCode_v2() {
        return code_v2;
    }

    public void setCode_v2(String code_v2) {
        this.code_v2 = code_v2;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }
}
