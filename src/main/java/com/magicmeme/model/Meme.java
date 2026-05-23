package com.magicmeme.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Meme {

    private String id;
    private String imageDataUrl;
    private String topText;
    private String bottomText;
    private String template;
    private String tone;
    private ConcurrentHashMap<String, Integer> reactions;
    private LocalDateTime createdAt;

    public Meme() {
    }

    public Meme(String id, String imageDataUrl, String topText, String bottomText, String template,
                String tone, Map<String, Integer> reactions, LocalDateTime createdAt) {
        this.id = id;
        this.imageDataUrl = imageDataUrl;
        this.topText = topText;
        this.bottomText = bottomText;
        this.template = template;
        this.tone = tone;
        this.reactions = new ConcurrentHashMap<>(reactions);
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageDataUrl() {
        return imageDataUrl;
    }

    public void setImageDataUrl(String imageDataUrl) {
        this.imageDataUrl = imageDataUrl;
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

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public ConcurrentHashMap<String, Integer> getReactions() {
        return reactions;
    }

    public void setReactions(ConcurrentHashMap<String, Integer> reactions) {
        this.reactions = reactions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
