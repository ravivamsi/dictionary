package com.example.abdulsamad.dictionaryapp;

/**
 * Created by ABDUL Samad on 6/23/2017.
 */

public class ResultProvider {
    String word,meaning;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {

        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public ResultProvider(String word, String meaning) {

        this.word = word;
        this.meaning = meaning;
    }
}
