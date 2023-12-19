package org.jquran.jquran;

public class CustomThing {
    private String surahName;
    private String surahInfo;
    private int firstPage;

    public String getName() {
        return surahName;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public String getSurahInfo() {
        return surahInfo;
    }

    public CustomThing(String name, String price, int firstPage) {
        super();
        this.surahName = name;
        this.surahInfo = price;
        this.firstPage = firstPage;
    }
}
