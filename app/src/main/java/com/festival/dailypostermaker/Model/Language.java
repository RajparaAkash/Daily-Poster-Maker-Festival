package com.festival.dailypostermaker.Model;

public class Language {

    public String langName;
    public String langeCode;
    public boolean isSelected;

    public Language(String langName, String langeCode, boolean isSelected) {
        this.langName = langName;
        this.langeCode = langeCode;
        this.isSelected = isSelected;
    }
}
