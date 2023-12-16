package org.jquran.jquran;

public class Word {
    private int id, position, page_number, line_number;
    private String audio_url, char_type_name, location, text_uthmani, text_imlaei, verse_key, code_v1, code_v2, text;
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

    public String getChar_type_name() {
        return char_type_name;
    }

    public void setChar_type_name(String char_type_name) {
        this.char_type_name = char_type_name;
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

    public String getText_imlaei() {
        return text_imlaei;
    }

    public void setText_imlaei(String text_imlaei) {
        this.text_imlaei = text_imlaei;
    }

    public String getVerse_key() {
        return verse_key;
    }

    public void setVerse_key(String verse_key) {
        this.verse_key = verse_key;
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
