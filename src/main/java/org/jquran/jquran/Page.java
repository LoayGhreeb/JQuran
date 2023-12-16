package org.jquran.jquran;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties (ignoreUnknown = true)
public class Page {
    private List<Verse> verses;

    public List<Verse> getVerses() {
        return verses;
    }

    public String getVersesAsString(){
        StringBuilder pageVerses = new StringBuilder();
        for(Verse verse : verses)
            pageVerses.append(verse.getCode_v1()).append(' ');
        return pageVerses.toString();
    }
    public void setVerses(List<Verse> verses) {
        this.verses = verses;
    }
}