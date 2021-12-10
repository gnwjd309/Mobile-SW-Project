package com.example.mobilepjapp.note;

/**
 * 리스트 뷰의 아이템(데이터)을 관리하는 클래스입니다.
 * 여기서 아이템은 STT의 항목(화자1:문장1) 입니다.
 **/
public class ListItem {
    private String speaker;     // 화자
    private String sentence;    // 문장

    public ListItem(String speaker, String sentence) {
        this.speaker = speaker;
        this.sentence = sentence;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}
