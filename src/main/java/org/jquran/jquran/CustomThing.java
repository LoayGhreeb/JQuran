package org.jquran.jquran;

public class CustomThing {
    private String surahName;
    private String surahInfo;

    public String getName() {
        return surahName;
    }

    public String getSurahInfo() {
        return surahInfo;
    }

    public CustomThing(String name, String price) {
        super();
        this.surahName = name;
        this.surahInfo = price;
    }
}
