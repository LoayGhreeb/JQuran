package org.jquran.jquran;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Word {
    private int id, position, verse_id, page_number, line_number;
    private String audio_url, verse_key, location, text_uthmani, code_v1, code_v2, qpc_uthmani_hafs, char_type_name, text;

    private Translation translation;
    private Transliteration transliteration;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getVerse_id() {
        return verse_id;
    }

    public void setVerse_id(int verse_id) {
        this.verse_id = verse_id;
    }

    public int getPage_number() {
        return page_number;
    }

    public void setPage_number(int page_number) {
        this.page_number = page_number;
    }

    public int getLine_number() {
        return line_number;
    }

    public void setLine_number(int line_number) {
        this.line_number = line_number;
    }

    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }

    public String getVerse_key() {
        return verse_key;
    }

    public void setVerse_key(String verse_key) {
        this.verse_key = verse_key;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getText_uthmani() {
        return text_uthmani;
    }

    public void setText_uthmani(String text_uthmani) {
        this.text_uthmani = text_uthmani;
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

    public String getCode(int codeVersion){
        if(codeVersion == 1) return code_v1;
        return code_v2;
    }

    public void setCode_v2(String code_v2) {
        this.code_v2 = code_v2;
    }

    public String getQpc_uthmani_hafs() {
        return qpc_uthmani_hafs;
    }

    public void setQpc_uthmani_hafs(String qpc_uthmani_hafs) {
        this.qpc_uthmani_hafs = qpc_uthmani_hafs;
    }

    public String getChar_type_name() {
        return char_type_name;
    }

    public void setChar_type_name(String char_type_name) {
        this.char_type_name = char_type_name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Translation getTranslation() {
        return translation;
    }

    public void setTranslation(Translation translation) {
        this.translation = translation;
    }

    public Transliteration getTransliteration() {
        return transliteration;
    }

    public void setTransliteration(Transliteration transliteration) {
        this.transliteration = transliteration;
    }
}