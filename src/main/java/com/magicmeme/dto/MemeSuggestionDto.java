package com.magicmeme.dto;

public class MemeSuggestionDto {

    private String id;
    private String title;
    private String tone;
    private String topText;
    private String bottomText;
    private String reason;

    public MemeSuggestionDto() {
    }

    public MemeSuggestionDto(String id, String title, String tone, String topText, String bottomText, String reason) {
        this.id = id;
        this.title = title;
        this.tone = tone;
        this.topText = topText;
        this.bottomText = bottomText;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getTopText() {
        return topText;
    }

    public void setTopText(String topText) {
        this.topText = topText;
    }

    public String getBottomText() {
        return bottomText;
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
