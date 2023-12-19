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


    public String getVersesByLine(int fontVersion) {
        List<StringBuilder> lines = new ArrayList<>();
        for(int i = 0; i < 16; i++)
            lines.add(new StringBuilder());

        for (Verse verse : verses) {
            for (Word word : verse.getWords()) {
                int lineNumber = word.getLine_number();
                lines.get(lineNumber).append(word.getCode(fontVersion)).append(' ');
            }
        }

        return String.join("\n", lines.subList(1, 16));
    }

    public void setVerses(List<Verse> verses) {
        this.verses = verses;
    }
}