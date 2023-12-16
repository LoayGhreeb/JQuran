package org.jquran.jquran;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties (ignoreUnknown = true)
public class Page {
    private List<Verse> verses;

    public List<Verse> getVerses() {
        return verses;
    }

    public String getVersesAsString(){
        StringBuilder pageVerses = new StringBuilder();
        for(Verse verse : verses) {
            pageVerses.append(verse.getCode_v2()).append(' ');
            System.out.println(verse.getPage_number());
        }
        return pageVerses.toString();
    }
    public String getVersesByLine(){
        StringBuilder pageVerses = new StringBuilder();
        ArrayList<StringBuilder> lines = new ArrayList<>(17);
        for (int i = 1; i <= 16; i++) {
            lines.add(new StringBuilder());
        }
        for(Verse verse : verses){
            for(Word word : verse.getWords()){
               lines.get(word.getLine_number()).append(word.getCode_v2()).append(' ');
            }
            System.out.println(verse.getSajdah_number());
        }
        for (int i = 1; i <= 15; i++) {
            if(lines.get(i) != null) {
                pageVerses.append(lines.get(i));
                pageVerses.append("\n");
            }
        }
        return pageVerses.toString();
    }
    public void setVerses(List<Verse> verses) {
        this.verses = verses;
    }
}